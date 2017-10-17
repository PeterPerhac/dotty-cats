import cats.implicits._
import cats.kernel.Monoid

import scala.util.Random._
import scala.util.Try

object CombiningExercise {

  final class Kilograms(val kg: Double) extends AnyVal with Ordered[Kilograms] {

    override def toString: String = f"$kg%.2f kg"

    override def compare(that: Kilograms): Int = this.kg.compareTo(that.kg)

    def +(that: Kilograms): Kilograms = (this.kg + that.kg).kg

  }

  implicit class KgOps[N: Numeric](n: N) {
    def kg = new Kilograms(implicitly[Numeric[N]].toDouble(n))
  }

  case class Crate(weight: Kilograms, contents: String)

  object Crate {
    def ofRandomContents(weight: Double) = Crate(weight.kg, nextString(10))
  }

  private def fetchCrate(s: String): Option[Crate] =
    Try(s.toDouble).toOption.filter(_ > 0).map(Crate.ofRandomContents)

  private def loadCrates(args: Array[String]) =
    args.map(fetchCrate).toVector

  def main(args: Array[String]): Unit = {
    val crates: Vector[Option[Crate]] = loadCrates(args)

    //lightweight crates weigh at most 20 kg
        val lightCratesTotalWeight: Kilograms = ???

    //stair-stepping, number of intermediary collections built along the way
    //    val lightCratesTotalWeight: Kilograms =
    //      crates.flatten.map(_.weight.kg).filter( _ <= 20 ).sum.kg
    //    val lightCratesTotalWeight: Kilograms =
    //      crates.flatten.map(_.weight).filter(_ <= 20.kg).fold(0.kg)(_ + _)

    // collect-based solution without Monoid
    // requires to build a foldable collection of crate weights before folding it into the final result
    //    val lightCratesTotalWeight: Kilograms = crates.collect {
    //      case Some(Crate(weight, _)) if weight <= 20.kg => weight
    //    }.fold(0.kg)(_ + _)

    // collect-based solution with a Monoid instance that could look like this:
    //    implicit object kiloAdder extends Monoid[Kilograms] {
    //      def empty: Kilograms = 0.kg
    //
    //      def combine(x: Kilograms, y: Kilograms): Kilograms = x + y
    //    }
    // still requires to build a list or vector of crate weights before combining
    //    val lightCratesTotalWeight: Kilograms = crates.collect {
    //      case Some(Crate(weight, _)) if weight <= 20.kg => weight
    //    }.combineAll


    // similar to above but not using the cats' provided syntax
    //    val lightCratesTotalWeight: Kilograms = Monoid[Kilograms].combineAll(crates.collect {
    //      case Some(Crate(weight, _)) if weight <= 20.kg => weight
    //    })

    //using an Addition Monoid for Kilograms we can foldMap the collection of all crates in one step
    //   val lightCratesTotalWeight: Kilograms = crates.foldMap {
    //     case Some(Crate(weight, _)) if weight <= 20.kg => weight
    //     case _ => Monoid[Kilograms].empty
    //   }

    println(lightCratesTotalWeight)
  }

}