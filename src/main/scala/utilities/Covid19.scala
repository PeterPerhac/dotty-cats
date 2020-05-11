package utilities

import java.net.URL
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate}

import cats.data.NonEmptyVector
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Reads, _}
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.collection.BSONSerializationPack.Writer
import reactivemongo.api.{DefaultDB, MongoConnection}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}

case class CovidDataLine(
      date: LocalDate,
      cases: Int,
      deaths: Int,
      country: String,
      countryCode: String,
      population: Option[Long]
) {
  def cumulativeStats: CumulativeStats = CumulativeStats(date, cases, cases, deaths, deaths)
}

object CovidDataLine {

  val customLocalDateReads: Reads[LocalDate] = Reads {
    case JsString(value) =>
      Try(LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy")))
        .orElse(Try(LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
        .fold[JsResult[LocalDate]](_ => JsError(s"can't parse date format: $value"), date => JsSuccess(date))
    case value => JsError(s"expected a date string but got: $value")
  }

  implicit val format: Reads[CovidDataLine] = (
    (__ \ "dateRep").read[LocalDate](customLocalDateReads) and
      (__ \ "cases").read[String].map(_.toInt) and
      (__ \ "deaths").read[String].map(_.toInt) and
      (__ \ "countriesAndTerritories").read[String].map(_.replaceAll("_", " ")) and
      (__ \ "countryterritoryCode").read[String] and
      (__ \ "popData2018").read[String].map(s => Option(s).filter(_.nonEmpty).flatMap(nes => Try(nes.toLong).toOption))
  )(CovidDataLine.apply _)

}

case class CumulativeStats(date: LocalDate, newCases: Int, totalCases: Int, newDeaths: Int, totalDeaths: Int)

case class Country(name: String, population: Option[Long], stats: Vector[CumulativeStats])
object Country {
  implicit object CountryWriter extends Writer[Country] {
    override def writeTry(t: Country): Try[BSONDocument] = Success(
      BSONDocument(
        List(
          Some("name" -> BSONString(t.name)),
          t.population.map(p => "population" -> BSONLong(p)),
          Some("situationReports" -> BSONArray(t.stats.map { cs =>
            BSONDocument(
              List(
                "date"        -> BSONString(cs.date.toString),
                "newCases"    -> BSONInteger(cs.newCases),
                "totalCases"  -> BSONInteger(cs.totalCases),
                "newDeaths"   -> BSONInteger(cs.newDeaths),
                "totalDeaths" -> BSONInteger(cs.totalDeaths)
              )
            )
          })),
          Some("timestamp" -> BSONDateTime(Instant.now().toEpochMilli))
        ).flatten)
    )
  }

}

object Covid19 extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val jsonUrl = """https://opendata.ecdc.europa.eu/covid19/casedistribution/json/"""
//  val jsonUrl = """file:////Users/peterperhac/my/scala/dotty-cats/covid-files/covid-data-dev.json"""
  val mongoEnabled = true

  lazy val driver = new reactivemongo.api.AsyncDriver
  lazy val connection: Future[MongoConnection] = driver.connect(List("localhost"))
  lazy val db: Future[DefaultDB] = connection.flatMap(_.database("covid-19"))
  def collection: Future[BSONCollection] = db.map(_.collection("countries"))

  //    lazy val mongoClient: MongoClient = MongoClient()
//  lazy val database: MongoDatabase = mongoClient.getDatabase("covid-19")

  // format: off
  val countryFilter: String => Boolean = Set(
    "AUT" /*	Austria */, "BEL" /*	Belgium */, "BRA" /*  Brazil */, "CAN" /*  Canada */, "CHE" /*	Switzerland */,
    "CHN" /*  China */, "CZE" /*	Czechia */, "DEU" /*	Germany */, "ESP" /*	Spain */, "FRA" /*	France */,
    "GBR" /*	United Kingdom */, "GRC" /*	Greece */, "HRV" /*	Croatia */, "HUN" /*	Hungary */, "IND" /*  India */,
    "IRL" /*	Ireland */, "ITA" /*	Italy */, "NLD" /*	Netherlands */, "POL" /*	Poland */, "RUS" /*	Russia */,
    "SVK" /*	Slovakia */, "SWE" /* Sweden */, "USA" /*  United States of America */
  ).contains
  // format: on

  val countriesOfInterest: Country => Boolean = c =>
    Set(
      "Slovakia",
      "United Kingdom",
      "Spain",
      "Germany",
      "United States of America",
      "Canada",
      "Italy",
      "Ireland",
      "Sweden",
      "Switzerland"
    ).contains(c.name)

  val tempFilePath = Paths.get("./covid-files/temp.json")

  val in = new URL(jsonUrl).openStream()
  try {
    Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING)

    val recordsByCountry: Map[String, Vector[CovidDataLine]] =
      Json
        .parse(Files.newInputStream(tempFilePath))
        .as[Vector[CovidDataLine]]((__ \ "records").read)
        .filter(l =>
          countryFilter(l.countryCode) && (l.date.isAfter(LocalDate.of(2020, 3, 1)) || l.cases > 0 || l.deaths > 0))
        .sortBy(entry => (entry.country, entry.date.toEpochDay))
        .groupBy(_.country)

    val countries = recordsByCountry.toVector.map {
      case (country, stats) =>
        val rs = NonEmptyVector.fromVectorUnsafe(stats)
        Country(
          name = country,
          population = stats.head.population, //unsafe head, but actually safe
          stats = rs.tail.foldLeft(Vector(rs.head.cumulativeStats)) {
            case (acc, r) =>
              acc :+ CumulativeStats(
                date = r.date,
                newCases = r.cases,
                totalCases = acc.last.totalCases + r.cases,
                newDeaths = r.deaths,
                totalDeaths = acc.last.totalDeaths + r.deaths
              )
          }
        )
    }

    import cats.implicits._

    def mongoArchivingAction: Future[String] =
      collection
        .flatMap(coll => countries.traverse_(c => coll.insert(false).one(c)))
        .map(_ => "successfully archived to mongo")

    countries.filter(countriesOfInterest).foreach { c =>
      println("""
                |===
                |""".stripMargin)
      println(f"${c.name}%s [pop ${c.population.getOrElse(0L)}%,d]")
      c.stats.foreach { s =>
        val infectedPercent =
          f"${100 * s.totalCases / c.population.fold[Double](s.totalDeaths.toDouble)(_.toDouble)}%,.4f"
        val deathRate = f"${100 * s.totalDeaths / s.totalCases.toDouble}%,.4f"

        println(
          s"${c.name}, ${s.date}, ${s.totalCases}, ${s.newCases}, ${s.totalDeaths}, ${s.newDeaths}, $infectedPercent, $deathRate"
        )
      }
    }

    if (mongoEnabled) {
      Await
        .ready(mongoArchivingAction, 10.seconds)
        .recover { case t => println(s"something went awry: $t") }
        .foreach(println)
    }

  } finally {
    in.close()
    Files.delete(tempFilePath)
    if (mongoEnabled) {
      Await.result(db.flatMap(_ => connection.map(_.close()(1.second))).flatMap(_ => driver.close(1.second)), 1.second)
    }
  }

}
