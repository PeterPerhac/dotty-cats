package io.underscore.advanced

object WriterMonadPractical {

  def main(args: Array[String]): Unit = {

    import cats.data.Writer
    //used to import applicative instance for Vector
    import cats.instances.vector._
    //used to add .pure method on Int
    import cats.syntax.applicative._
    //used to add .tell method on Vector
    import cats.syntax.writer._

    type Logged[A] = Writer[Vector[String], A]

    def slowly[A](body: => A) =
      try body
      finally Thread.sleep(100)

    def factorial(n: Int): Logged[Int] =
      if (n == 0) {
        1.pure[Logged]
      } else {
        for {
          a <- slowly(factorial(n - 1))
          _ <- Vector(s"fact $n ${a * n}").tell
        } yield a * n
      }

    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent._
    import scala.concurrent.duration._
    Await.result(
      Future.sequence(
        Vector(
          Future(factorial(5)),
          Future(factorial(5))
        )),
      Duration.Inf)

    val f5: Logged[Int] = factorial(5)

    val Vector((logA, ansA), (logB, ansB), (logC, ansC)) =
      Await.result(
        Future.sequence(
          Vector(
            Future(f5.run),
            Future(factorial(10).run),
            Future(factorial(7).run)
          )),
        Duration.Inf)

    println(logA)
    println(ansA)
    println("----")
    println(logB)
    println(ansB)
    println("----")
    println(logC)
    println(ansC)
    println("----")
  }
}
