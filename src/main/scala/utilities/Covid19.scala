package utilities

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files._
import java.nio.file.{Files, Paths}
import java.time.LocalDate

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

  val countriesOfInterest : Set[String] = Set("Slovakia", "The United Kingdom", "Germany") //, "France", "Czechia") //, "Slovakia")

  println("Country, Date, Total Cases, New Cases, Total Deaths, New Deaths")
  for {
    stat <- sortedStatistics if countriesOfInterest.contains(stat.country)
    CovidStat(d, r, c, tc, nc, td, nd) = stat
  } println(s"$c, $d, $tc, $nc, $td, $nd")

}
