package io.underscore.advanced

import cats.data.Writer

object WriterMonad {

  def main(args: Array[String]): Unit = {
    import cats.syntax.writer._
    val writer = 123.writer(Vector("msg1", "msg2", "msg3"))
    println(writer.value)
    println(writer.written)

    import cats.instances.vector._
    import cats.syntax.applicative._
    type Logged[A] = Writer[Vector[String], A]

    val writer2 = for {
      a <- 10.pure[Logged]
      _ <- Vector("a", "b", "c").tell
      b <- 32.writer(Vector("x", "y", "z"))
    } yield a + b

    val (log, value) = writer2.run
    println(s"log: $log")
    println(s"value: $value")
  }
}
