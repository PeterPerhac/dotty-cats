import cats.Monoid

import scala.util.Random._

object CombiningExercise2 {

  final class Kilograms(val kg: Double) extends AnyVal with Ordered[Kilograms] {
    override def toString: String = f"$kg%.3f kg"

    override def compare(that: Kilograms): Int = this.kg.compareTo(that.kg)

    def +(that: Kilograms): Kilograms = (this.kg + that.kg).kg
  }

  implicit class KgOps[N: Numeric](n: N) {
    def kg = new Kilograms(implicitly[Numeric[N]].toDouble(n))
  }

  implicit def kgAddMonoid(implicit m: Monoid[Double]): Monoid[Kilograms] =
    new Monoid[Kilograms] {
      override def empty: Kilograms = m.empty.kg

      override def combine(x: Kilograms, y: Kilograms): Kilograms = m.combine(x.kg, y.kg).kg
    }

  val odd: (Int) => Boolean = (n: Int) => n % 2 == 1

  case class Crate(weight: Kilograms, contents: String)

  val MAX_WEIGHT = 50

  private val fetchCrate = (n: Int) =>
    Some(nextInt(5)).filter(odd).map(_ => Crate((nextDouble() * MAX_WEIGHT).kg, nextString(10)))

  private val crates: Seq[Option[Crate]] = 0 to 100 map fetchCrate

  def main(args: Array[String]): Unit = {
    //medium crates weigh >= 10 kg and < 25kg
    val kgTotalMidSized: Kilograms = crates.flatten.map(_.weight).filter(kg => kg >= 10.kg && kg < 25.kg).fold(0.kg)(_ + _)
    println(kgTotalMidSized)
  }

}