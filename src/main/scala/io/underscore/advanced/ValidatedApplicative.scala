package io.underscore.advanced

import cats.data.Validated

import scala.util.Try

object ValidatedApplicative {

  def main(args: Array[String]): Unit = {

    type ErrorsOr[A] = Validated[Vector[String], A]

    import cats.instances.vector._
    import cats.syntax.applicative._
    import cats.syntax.apply._
    import cats.syntax.validated._

    def errOrA1(): ErrorsOr[Int] = "this is not ok".pure.invalid
    def errOrA2(): ErrorsOr[Int] = 2.valid
    def errOrA3(): ErrorsOr[Int] = Validated.fromTry(Try("foo".toInt)).leftMap(t => s"exception in: ${t.getMessage}".pure)
    def errOrA4(): ErrorsOr[Int] = 4.valid
    def errOrA5(): ErrorsOr[Int] = "this is plain wrong!".pure.invalid

    val foo = (errOrA1(), errOrA2(), errOrA3(), errOrA4(), errOrA5()).mapN {
      case (a1, a2, a3, a4, a5) => s"$a1-$a2-$a3-$a4-$a5"
    }

    foo.fold(println, println)
  }

}
