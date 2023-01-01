import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, LocalDate, Month, Year}
import java.time.DayOfWeek._
import java.time.Month._

object Dates extends App {

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.")

  def datesInYear(year: Year): Seq[LocalDate] = (1 to year.length()).map(year.atDay)

  val skMonthName: Month => String = {
    case JANUARY   => "JANUÁR"
    case FEBRUARY  => "FEBRUÁR"
    case MARCH     => "MAREC"
    case APRIL     => "APRÍL"
    case MAY       => "MÁJ"
    case JUNE      => "JÚN"
    case JULY      => "JÚL"
    case AUGUST    => "AUGUST"
    case SEPTEMBER => "SEPTEMBER"
    case OCTOBER   => "OKTÓBER"
    case NOVEMBER  => "NOVEMBER"
    case DECEMBER  => "DECEMBER"
  }

  val skDayOfWeek: DayOfWeek => String = {
    case MONDAY    => "Po"
    case TUESDAY   => "Ut"
    case WEDNESDAY => "St"
    case THURSDAY  => "Št"
    case FRIDAY    => "Pi"
    case SATURDAY  => "So"
    case SUNDAY    => "Ne"
  }

  (2023 to 2050).foreach { yr =>

    val pw: PrintWriter = new PrintWriter(s"$yr.md", StandardCharsets.UTF_8)
    try {
      datesInYear(Year.of(yr)).foreach { d =>
        val dayOfWeek = d.getDayOfWeek

        val formattedString = s"${d.format(formatter)} (${skDayOfWeek(d.getDayOfWeek)})"

        if (d.getDayOfMonth == 1) {
          pw.println()
          pw.println(s"**${skMonthName(d.getMonth)}**")
          pw.println()
        }

        pw.println(formattedString)

        if (dayOfWeek == SUNDAY) {
          pw.println()
          pw.println()
        }
      }
    }
    finally {
      pw.close()
    }
  }
}
