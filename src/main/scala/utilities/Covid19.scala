package utilities

import java.net.URL
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.data.NonEmptyVector
import play.api.libs.json.{Json, Reads}

import scala.collection.MapView
import scala.util.Try

case class CovidData(records: Vector[CovidDataLine])

object CovidData {
  implicit val format: Reads[CovidData] = Json.reads
}

case class CovidDataLine(
      date: LocalDate,
      cases: Int,
      deaths: Int,
      country: String,
      countryCode: String,
      population: Option[Int]
) {
  override def toString: String = s"$country, $date, $cases, $deaths"
}

case class CovidSimpleStat(date: LocalDate, country: String, cases: Int, deaths: Int)

case class Reading(date: LocalDate, cases: Int, deaths: Int) {
  override def toString: String = s"$date, $cases, $deaths"
}

case class CumulativeStats(date: LocalDate, newCases: Int, totalCases: Int, newDeaths: Int, totalDeaths: Int) {
  override def toString: String = s"$date, $totalCases, $newCases, $totalDeaths, $newDeaths"
}

object CumulativeStats {
  def start(r: Reading): CumulativeStats =
    CumulativeStats(r.date, r.cases, r.cases, r.deaths, r.deaths)
}

object CovidDataLine {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  val customLocalDateReads: Reads[LocalDate] = Reads {
    case JsString(value) =>
      Try(LocalDate.parse(value, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        .fold[JsResult[LocalDate]](_ => JsError("can't parse date format"), JsSuccess(_))
    case _ => JsError("expected a date string")
  }

  implicit val format: Reads[CovidDataLine] = (
    (__ \ "dateRep").read[LocalDate](customLocalDateReads) and
      (__ \ "cases").read[String].map(_.toInt) and
      (__ \ "deaths").read[String].map(_.toInt) and
      (__ \ "countriesAndTerritories").read[String].map(_.replaceAll("_", " ")) and
      (__ \ "countryterritoryCode").read[String] and
      (__ \ "popData2018").read[String].map(s => Option(s).filter(_.nonEmpty).flatMap(nes => Try(nes.toInt).toOption))
  )(CovidDataLine.apply _)
}

object Covid19 extends App {

  val jsonUrl = """https://opendata.ecdc.europa.eu/covid19/casedistribution/json/"""
  //  val jsonUrl = """file:////Users/peterperhac/my/scala/dotty-cats/covid-files/covid-data-dev.json"""

  val countryFilter: String => Boolean = Set(
    "ALB" /*	Albania */,
    "AND" /*	Andorra */,
    "AUS" /* Australia */,
    "AUT" /*	Austria */,
    "BEL" /*	Belgium */,
    "BGR" /*	Bulgaria */,
    "BIH" /*	Bosnia and Herzegovina */,
    "BLR" /*	Belarus */,
    "BRA" /* Brazil */,
    "CAN" /* Canada */,
    "CHE" /*	Switzerland */,
    "CHN" /* China */,
    "CYP" /*	Cyprus */,
    "CZE" /*	Czech */,
    "DEU" /*	Germany */,
    "DNK" /*	Denmark */,
    "ESP" /*	Spain */,
    "EST" /*	Estonia */,
    "FIN" /*	Finland */,
    "FRA" /*	France */,
    "FRO" /*	Faroe Islands */,
    "GBR" /*	United Kingdom */,
    "GIB" /*	Gibraltar */,
    "GRC" /*	Greece */,
    "HRV" /*	Croatia */,
    "HUN" /*	Hungary */,
    "IMN" /*	Isle of Man */,
    "IND" /* India */,
    "IRL" /*	Ireland */,
    "ISL" /*	Iceland */,
    "ITA" /*	Italy */,
    "LIE" /*	Liechtenstein */,
    "LTU" /*	Lithuania */,
    "LUX" /*	Luxembourg */,
    "LVA" /*	Latvia */,
    "MCO" /*	Monaco */,
    "MDA" /*	Moldova */,
    "MEX" /* Mexico */,
    "MKD" /*	Macedonia */,
    "MLT" /*	Malta */,
    "MNE" /*	Montenegro */,
    "NLD" /*	Netherlands */,
    "NOR" /*	Norway */,
    "POL" /*	Poland */,
    "PRT" /*	Portugal */,
    "ROU" /*	Romania */,
    "RUS" /*	Russia */,
    "SMR" /*	San Marino */,
    "SRB" /*	Serbia */,
    "SVK" /*	Slovakia */,
    "SVN" /*	Slovenia */,
    "SWE" /*	Sweden */,
    "UKR" /*	Ukraine */,
    "USA" /* United States of America */,
    "VAT" /*	Vatican */,
    "XKX" /*	Kosovo */
  ).contains

  val countriesOfInterest: String => Boolean =
    Set(
      "Slovakia",
      "United Kingdom",
      "Spain",
      "Germany",
      "United States of America",
      "Italy",
      "Switzerland",
      "Brazil",
      "Czech Republic",
      "India"
    ).contains

  val tempFilePath = Paths.get("./covid-files/temp.json")
  val startDate = LocalDate.of(2019, 12, 1)
  val today = LocalDate.now()

  val in = new URL(jsonUrl).openStream()
  try {
    Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING)
    val data = Json.parse(Files.newInputStream(tempFilePath)).as[CovidData]
    val recordsByCountry: MapView[String, NonEmptyVector[Reading]] =
      data.records
        .collect {
          case r if countryFilter(r.countryCode) && (r.cases > 0 || r.deaths > 0) =>
            CovidSimpleStat(r.date, r.country, r.cases, r.deaths)
        }
        .sortBy(css => (css.country, css.date.toEpochDay))
        .groupBy(_.country)
        .view
        .mapValues(readings => NonEmptyVector.fromVectorUnsafe(readings.map(s => Reading(s.date, s.cases, s.deaths))))

    val printHistory: (String, NonEmptyVector[Reading]) => Unit = {
      case (c, rs) =>
        val dataWithCumulativeNumbers = rs.foldLeft(Vector(CumulativeStats.start(rs.head))) {
          case (acc, r) =>
            acc :+ CumulativeStats(
              r.date,
              r.cases,
              acc.last.totalCases + r.cases,
              r.deaths,
              acc.last.totalDeaths + r.deaths
            )
        }
        println(s"""${dataWithCumulativeNumbers.map(r => s"$c, $r").mkString("\n")}
                   |
                   |=======
                   |""".stripMargin)
    }

    val countryData = recordsByCountry.filterKeys(countriesOfInterest)
    countryData.keySet.toList.sorted.foreach(country => printHistory(country, countryData(country)))
  } finally {
    in.close()
    Files.delete(tempFilePath)
  }

}
