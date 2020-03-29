import java.time.Month._
import java.time.temporal.ChronoUnit
import java.time.{LocalDate, Month}
import java.time.LocalDate.{of => date}

object Mortgage {

  type InterestRate = LocalDate => Double
  type Repayment = PartialFunction[LocalDate, Double]

  val SELL_YEAR = 2022
  val SELL_PRICE = 325000.0
  val YEARLY_OVERPAYMENT = 10000
  val STARTING_POINT = PointInTime(date(2017, Month.AUGUST, 31), 283500.0)

  val interestRate: InterestRate = {
    case dt if dt.isBefore(date(2022, Month.JUNE, 30)) => 1.99
    case _                                             => 4.00
  }
  val dailyInterestRate: InterestRate = interestRate andThen (_ / 36500)

  def repayment(date: LocalDate, amt: Double): Repayment = {
    case d if d.equals(date) => amt
  }

  val monthlyRepayments: List[Repayment] = List(
    repayment(date(2017, OCTOBER, 1), 953.14), //real
    repayment(date(2017, NOVEMBER, 1), 937.68), //real
    repayment(date(2017, DECEMBER, 1), 937.68), //real
    repayment(date(2018, JANUARY, 1), 937.68), //real
    repayment(date(2018, FEBRUARY, 1), 2000.00), //real
    repayment(date(2018, MARCH, 1), 2000.00), //real
    repayment(date(2018, APRIL, 1), 2000.00), //real
    repayment(date(2018, MAY, 1), 2000.00), //real
    repayment(date(2018, JUNE, 1), 2000.00), //real
    repayment(date(2018, JULY, 1), 2000.00), //real
    repayment(date(2018, AUGUST, 1), 2000.00), //real
    repayment(date(2018, SEPTEMBER, 1), 2000.00), //real
    repayment(date(2018, OCTOBER, 1), 2000.00), //real
    {
      case d if d.isAfter(LocalDate.now) && d.isBefore(date(2022, JUNE, 30)) && d.getDayOfMonth == 1 => 937.68
    }, //
    {
      case d if d.getDayOfMonth == 1 => 1150
    }
  )

  val oneOffs: List[Repayment] = List(
    repayment(date(2018, JANUARY, 4), 10000.00), //real
    repayment(date(2018, OCTOBER, 10), 10000.00) //real
  ) ++ (2019 to 2100).map(y => repayment(date(y, APRIL, 8), YEARLY_OVERPAYMENT)) ++
    (2019 to 2100).map(y => repayment(date(y, OCTOBER, 8), YEARLY_OVERPAYMENT))

  val repayNothing: Repayment = {
    case _ => 0.0
  }

  val repay: Repayment =
    (monthlyRepayments ++ oneOffs :+ repayNothing).reduceLeft(_ orElse _)

  val headerRow = "date     \tstill owe\tinterest\trepaid  \tsaved    \ttake home\ttotal cost\tratio"

  case class PaymentAndInterest(payment: Double, interest: Double)
  case class PointInTime(d: LocalDate, amountOwed: Double, history: List[PaymentAndInterest] = List.empty) {
    override def toString: String = {
      val interest = history.map(_.interest).sum
      val repaid = history.map(_.payment).sum
      val saved = repaid - interest
      val takeHome = SELL_PRICE - amountOwed
      val totalCost = amountOwed + repaid
      val ratio = totalCost / STARTING_POINT.amountOwed
      f"$d\t$amountOwed%,2.2f\t$interest%,2.2f\t$repaid%,2.2f\t$saved%,2.2f\t$takeHome%,2.2f\t$totalCost%,2.2f\t$ratio%,2.2f"
    }
  }

  def main(args: Array[String]): Unit = {

    val growingInterest = LazyList.unfold(STARTING_POINT) {
      case pit @ PointInTime(d, amt, pai) =>
        val tomorrow = d.plus(1, ChronoUnit.DAYS)
        val interest = dailyInterestRate(d) * amt
        val amtWithInterest = amt + interest
        val repayment = repay(tomorrow)
        val newAmt = amtWithInterest - repayment
        Option((pit, PointInTime(tomorrow, newAmt, PaymentAndInterest(repayment, interest) :: pai)))
          .filter {
            case (currentPoint, _) => currentPoint.amountOwed >= 0
          }
    }

    growingInterest
      .collect {
        case pit @ PointInTime(d(y, _, 1), _, history) if y <= SELL_YEAR => pit
      }
      .foreach {
        case pit @ PointInTime(d(y, SEPTEMBER, 1), _, _) =>
          println(s"---------\n$y/${y + 1}\n---------\n$headerRow\n$pit")
        case pit @ PointInTime(d(_, _, 1), _, _) => println(pit)
        case _                                   => ()
      }

  }

  object d {
    def unapply(localDate: LocalDate): Option[(Int, Month, Int)] =
      Some(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth)
  }

}
