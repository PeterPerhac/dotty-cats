package conditional_flatmap

import cats.Applicative
import scala.util.Random.nextInt

object ConditionalFlatMapExample extends ConditionalFlatMap {

  import cats.instances.option._

  def iPrint(i: Int) = Option { println(s"printing: $i") }
  def randomNumber() = Option { nextInt(84) }

  def program() = for {
    magicNum <- randomNumber()
    _        <- iPrint(magicNum)
  } yield magicNum

  def program2() = for {
    magicNum <- randomNumber()
    _        <- iPrint(magicNum) onlyIf magicNum > 42
  } yield magicNum

  def main(args: Array[String]): Unit = {
    (1 to 10).foreach( _=> program().map(println))
    println("=====================")
    (1 to 10).foreach( _=> program2().map(println))
  }

}

trait ConditionalFlatMap {

  implicit class CustomApplicativeOps[F[_], A](fa: => F[A])(implicit F: Applicative[F]) {

    def onlyIf(condition: Boolean): F[Unit] =
      if (condition) F.map(fa)(_ => ()) else F.pure(())

  }

}
