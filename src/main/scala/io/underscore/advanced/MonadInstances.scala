package io.underscore.advanced

object MonadInstances {

  def main(args: Array[String]): Unit = {
    import cats.Monad
    import cats.instances.list._
    import cats.instances.option._
    import cats.syntax.applicative._
    import cats.syntax.flatMap._
    import cats.syntax.functor._

    println(Monad[Option].flatMap(Option(1))(x => Option(x * 2)))

    println(1.pure[Option])
    println(1.pure[List])

    import scala.language.higherKinds
    def sumSquare[M[_] : Monad](a: M[Int], b: M[Int]): M[Int] = for {
      x <- a
      y <- b
    } yield x * x + y * y

    println(sumSquare(Option(3), Option(4)))
    println(sumSquare(List(1, 2, 3), List(4, 5)))
  }

}
