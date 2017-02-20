package io.underscore.advanced

import cats.data.State

object StateMonadPostOrderCalculator {

  type CalcState[A] = State[List[Double], A]

  def operator(op: (Double, Double) => Double): CalcState[Double] =
    State[List[Double], Double] {
      case d1 :: d2 :: tail =>
        val ret = op(d1, d2)
        (ret :: tail, ret)
      case _ => sys.error("invalid operand stack")
    }


  def operand(d: Double): CalcState[Double] =
    State[List[Double], Double] {
      stack => (d :: stack, d)
    }


  def evalOne(sym: String): CalcState[Double] = sym match {
    case "+" => operator(_ + _)
    case "-" => operator(_ - _)
    case "*" => operator(_ * _)
    case "/" => operator(_ / _)
    case n => operand(n.toDouble)
  }

  def evalAll(input: List[String]): CalcState[Double] = {
    import cats.syntax.applicative._
    input.foldLeft(0d.pure[CalcState]) { (a, b) =>
      a flatMap (_ => evalOne(b))
    }
  }

  def main(args: Array[String]): Unit = {
    val program = evalAll(List("1", "2", "+", "3", "*"))
    println(program.runA(Nil).value)

    val program2 = for {
      _ <- evalAll(List("1", "2", "+"))
      _ <- evalAll(List("3", "4", "+"))
      ans <- evalOne("-")
    } yield ans

    println(program2.runA(Nil).value)
  }

}
