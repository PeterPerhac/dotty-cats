import java.time.format.{DateTimeFormatter}
import java.time.{LocalDate, Year}

object Dates extends App {

  val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.");

  def allDaysForYear(year: Int): List[LocalDate] = {
    val daysInYear = if(Year.of(year).isLeap) 366 else 365
    (1 to daysInYear).map(LocalDate.ofYearDay(year.toInt, _)).toList
  }

  allDaysForYear(2019).foreach{ d =>
    println(d.format(formatter))
  }

}
