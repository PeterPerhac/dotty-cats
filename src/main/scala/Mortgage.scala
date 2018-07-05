import java.time.Month._
import java.time.temporal.ChronoUnit
import java.time.{LocalDate, Month}
import java.time.LocalDate.{of => date}

object Mortgage {

  type InterestRate = LocalDate => Double
  type Repayment = PartialFunction[LocalDate, Double]

  val interestRate: InterestRate = {
    case dt if dt.isBefore(date(2022, Month.JUNE, 30)) => 1.99
    case _ => 3.79
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
    {
      case d if d.isAfter(LocalDate.now) && d.getDayOfMonth == 1 => 2000.00
    }
  )

  val oneOffs: List[Repayment] = List(
    repayment(date(2018, JANUARY, 4), 10000.00), //real
    repayment(date(2019, JANUARY, 4), 10000.00),
    repayment(date(2020, JANUARY, 4), 10000.00),
    repayment(date(2021, JANUARY, 4), 10000.00)
  )

  val repayNothing: Repayment = {case _ => 0.0}

  val repay: Repayment =
    (monthlyRepayments ++ oneOffs :+ repayNothing).reduceLeft(_ orElse _)

  case class PointInTime(d: LocalDate, amountBorrowed: Double) {
    override def toString: String = f"$d\t$amountBorrowed%,2.2f"
  }

  def main(args: Array[String]): Unit = {
    val startingPoint = PointInTime(date(2017, Month.AUGUST, 31), 283500.0)

    val growingInterest = unfold(startingPoint) {
      case pit @ PointInTime(d, amt) =>
        val tomorrow = d.plus(1, ChronoUnit.DAYS)
        val newAmt = amt * (1.0 + dailyInterestRate(d)) - repay(tomorrow)
        Option(amt)
          .map(_ => (pit, PointInTime(tomorrow, newAmt)))
          .filter(_._1.amountBorrowed > 0.0)
    }

    growingInterest
      .foreach {
        case pit@ PointInTime(d(y, SEPTEMBER, 1),_) => println(s"---------\n$y/${y+1}\n----\n$pit")
        case pit@ PointInTime(d(_, _, 1), _) => println(pit)
        case _ => ()
      }

  }

  object d {
    def unapply(localDate: LocalDate): Option[(Int, Month, Int)] =
      Some(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth)
  }

  def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = f(z) match {
    case Some((h, s)) => Stream.cons(h, unfold(s)(f))
    case None         => Stream.empty
  }

}
