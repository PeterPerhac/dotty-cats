package io.underscore.advanced

import cats.Functor

object LiftingWithFunctors {

  def main(args: Array[String]): Unit = {
    import cats.instances.option._
    val func = (x: Int) => s"___${x}___"
    val lifted = Functor[Option].lift(func)
    println(lifted(Option(1)))
    foo
  }


  def foo = {
    import cats.instances.function._
    import cats.syntax.functor._
    val f1 = (x:Int) => x * 2
    val f2 = (x:Int) => x * x
    println(f1.map(f2)(10))
  }

}
