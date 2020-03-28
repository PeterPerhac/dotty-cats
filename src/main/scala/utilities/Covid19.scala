package utilities

import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files._
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.time.LocalDate

import play.api.libs.json.{Json, OFormat}

import scala.jdk.CollectionConverters._
import scala.util.Try

case class CovidStat(
      date: LocalDate,
      rank: Int,
      country: String,
      totalCases: Int,
      newCases: Int,
      totalDeaths: Int,
      newDeaths: Int
)

object Covid19 extends App {

  val LineRegex = """^(\d\d\d\d-\d\d-\d\d)\s(\d+)\s([\w\s]+)\s(\d+)\s(\d+)\s(\d+)\s(\d+).*$""".r

  val dirStream = Files.newDirectoryStream(Paths.get("./covid-files"), "covid19-*.txt")
  val files = dirStream.asScala.toVector
  dirStream.close()

  val allEntries: Vector[String] = files.flatMap { filePath =>
    readAllLines(filePath, UTF_8).asScala
  }

  val parseLine: String => Option[CovidStat] = {
    case LineRegex(ds, r, c, ct, cn, dt, dn) =>
      Try(CovidStat(LocalDate.parse(ds), r.toInt, c.trim, ct.toInt, cn.toInt, dt.toInt, dn.toInt)).toOption
    case _ => None
  }

  val printLine: CovidStat => Unit = println

  val sortedStatistics = allEntries.flatMap(parseLine).sortBy(stat => (stat.country, stat.date.toEpochDay, stat.rank))

  val countriesOfInterest: Set[String] = Set("Slovakia", "The United Kingdom", "Germany", "France", "Spain", "Italy")

  println("Country, Date, Total Cases, New Cases, Total Deaths, New Deaths")
  for {
    stat <- sortedStatistics if countriesOfInterest.contains(stat.country)
    CovidStat(d, r, c, tc, nc, td, nd) = stat
  } println(s"$c, $d, $tc, $nc, $td, $nd")

}


case class CovidData(records: Vector[CovidDataLine])

object CovidData {
  implicit val format: OFormat[CovidData] = Json.format
}

case class CovidDataLine(
                          day: String,
                          month: String,
                          year: String,
                          cases: String,
                          deaths: String,
                          countriesAndTerritories: String,
                          countryterritoryCode: String
                        ) {
  val country: String = countriesAndTerritories.replaceAll("_", " ")
  val d: LocalDate = LocalDate.of(year.toInt, month.toInt, day.toInt)

  override def toString: String = s"$country, $d, $cases, $deaths"
}

case class CovidSimpleStat(date: LocalDate, country: String, newCases: Int, newDeaths: Int)

object CovidDataLine {
  implicit val format: OFormat[CovidDataLine] = Json.format
}

object DownloadAndCovert extends App {

  //  val jsonUrl = """https://opendata.ecdc.europa.eu/covid19/casedistribution/json/"""
  val jsonUrl = """file:////Users/peterperhac/my/scala/dotty-cats/covid-files/covid-data-dev.json"""

  val countryFilter: Set[String] = Set(
    "ALB", //	Albania
    "AND", //	Andorra
    "AUS", // Australia
    "AUT", //	Austria
    "BEL", //	Belgium
    "BGR", //	Bulgaria
    "BIH", //	Bosnia and Herzegovina
    "BLR", //	Belarus
    "BRA", // Brazil
    "CAN", // Canada
    "CHE", //	Switzerland
    "CHN", // China
    "CYP", //	Cyprus
    "CZE", //	Czech
    "DEU", //	Germany
    "DNK", //	Denmark
    "ESP", //	Spain
    "EST", //	Estonia
    "FIN", //	Finland
    "FRA", //	France
    "FRO", //	Faroe Islands
    "GBR", //	United Kingdom
    "GIB", //	Gibraltar
    "GRC", //	Greece
    "HRV", //	Croatia
    "HUN", //	Hungary
    "IMN", //	Isle of Man
    "IND", // India
    "IRL", //	Ireland
    "ISL", //	Iceland
    "ITA", //	Italy
    "LIE", //	Liechtenstein
    "LTU", //	Lithuania
    "LUX", //	Luxembourg
    "LVA", //	Latvia
    "MCO", //	Monaco
    "MDA", //	Moldova
    "MEX", // Mexico
    "MKD", //	Macedonia
    "MLT", //	Malta
    "MNE", //	Montenegro
    "NLD", //	Netherlands
    "NOR", //	Norway
    "POL", //	Poland
    "PRT", //	Portugal
    "ROU", //	Romania
    "RUS", //	Russia
    "SMR", //	San Marino
    "SRB", //	Serbia
    "SVK", //	Slovakia
    "SVN", //	Slovenia
    "SWE", //	Sweden
    "UKR", //	Ukraine
    "USA", // United States of America
    "VAT", //	Vatican
    "XKX", //	Kosovo
  )

  val tempFilePath = Paths.get("./covid-files/temp.json")
  val startDate = LocalDate.of(2019, 12, 1)
  val today = LocalDate.now()

  val in = new URL(jsonUrl).openStream()
  try {
    Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING)
    val data = Json.parse(Files.newInputStream(tempFilePath)).as[CovidData]
    val recordsByCountry: Map[String, Vector[CovidSimpleStat]] =
      data.records.collect {
        case r if countryFilter.contains(r.countryterritoryCode) => CovidSimpleStat(r.d, r.country, r.cases.toInt, r.deaths.toInt)
      }.sortBy(css => (css.country, css.date.toEpochDay))
        .groupBy(_.country)


//    val dateStream = LazyList.unfold[LocalDate, LocalDate](startDate)(d => condOpt(d) {
//      case date if !date.isAfter(today) => (date, date.plusDays(1))
//    })

  } finally {
    in.close()
    Files.delete(tempFilePath)
  }

}
