package io.underscore.advanced

import cats.data.State

object StateMonad {

  def main(args: Array[String]): Unit = {
    import State._

    val program: State[Int, (Int, Int, Int)] = for {
      a <- get[Int]
      _ <- set[Int](a + 1)
      b <- get[Int]
      _ <- modify[Int](_ + 1)
      c <- inspect[Int, Int](_ * 1000)
    } yield (a, b, c)
    val (state, result) = program.run(1).value
    println(state)
    println(result)
  }

}
