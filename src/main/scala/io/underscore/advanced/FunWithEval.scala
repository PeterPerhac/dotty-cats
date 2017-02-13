package io.underscore.advanced

import cats.Eval

object FunWithEval {


  def main(args: Array[String]): Unit = {
    val ans = for {
      a <- Eval.now { println("Calculating A"); 35}
      b <- Eval.always { println("Calculating B"); 5}
      c <- Eval.later { println("Calculating C"); 2}
    } yield {
      println("Adding A and B and C")
      a + b + c
    }

    println(ans.value)
    println(ans.value)

    def factorial(n: BigInt): Eval[BigInt] = if (n == 1) Eval.now(n) else Eval.defer(factorial(n - 1).map(_ * n))

    println(factorial(50000).value)
  }

}
