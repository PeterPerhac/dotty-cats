package utilities

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, TextStyle}
import java.util.Locale
import scala.annotation.tailrec

object RotaBuilder {

  def main(args: Array[String]): Unit = {

    def formatDayOfWeek(date: LocalDate): String =
      date.getDayOfWeek.getDisplayName(TextStyle.SHORT, Locale.UK)

    @tailrec
    def findNextDate(d: LocalDate): LocalDate = {
      def isValidDate(cd: LocalDate): Boolean = !(skipDates.contains(cd) || skipDays.contains(formatDayOfWeek(cd)))
      val candidate: LocalDate = d.plusDays(1)
      if (isValidDate(candidate)) candidate else findNextDate(candidate)
    }

    val rota: Seq[Row] = LazyList
      .unfold[Row, (LocalDate, LazyList[String])]((LocalDate.now(), people)) {
        case (d, peeps) =>
          Some(Row(date = d, formatDayOfWeek(d), peeps.head), (findNextDate(d), peeps.tail))
      }

    rota.takeWhile(_.date.getYear < 2025).foreach(println)
  }

  def people: LazyList[String] = LazyList("Darrell", "Habeeb", "Jack", "Peter", "Reddy") #::: people

  val skipDays: Set[String] = Set("Fri", "Sat", "Sun")
  val skipDates: Set[LocalDate] = Set(
    LocalDate.of(2022, 5, 2), //early may bank holiday
    LocalDate.of(2022, 6, 2), //spring bank holiday
    LocalDate.of(2022, 6, 3), //platinum jubilee bank holiday
    LocalDate.of(2022, 8, 29), //summer bank holiday
    LocalDate.of(2022, 12, 26), //boxing day
    LocalDate.of(2022, 12, 27), //christmas day,
    LocalDate.of(2023, 1, 2), // New Year's Day
    LocalDate.of(2023, 4, 7), // Good Friday
    LocalDate.of(2023, 4, 10), // Easter Monday
    LocalDate.of(2023, 5, 1), // Early May Bank Holiday
    LocalDate.of(2023, 5, 29), // Spring Bank Holiday
    LocalDate.of(2023, 8, 28), // Summer Bank Holiday
    LocalDate.of(2023, 12, 25), // Christmas Day
    LocalDate.of(2023, 12, 26), // Boxing Day
    LocalDate.of(2024, 1, 1), // New Year's Day	
    LocalDate.of(2024, 3, 29), // Good Friday	
    LocalDate.of(2024, 4, 1), // Easter Monday	
    LocalDate.of(2024, 5, 6), // Early May Bank Holiday	
    LocalDate.of(2024, 5, 27), // Spring Bank Holiday	
    LocalDate.of(2024, 8, 26), // Summer Bank Holiday	Likely
    LocalDate.of(2024, 12, 25), // Christmas Day	
    LocalDate.of(2024, 12, 26) // Boxing Day	
  )

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

  case class Row(date: LocalDate, dayOfWeek: String, name: String) {
    override def toString: String = s"${date.format(dateFormatter)}, $dayOfWeek, $name"
  }

}
