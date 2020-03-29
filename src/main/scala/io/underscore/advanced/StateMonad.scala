package io.underscore.advanced

import cats.data.State

object StateMonad {

  case class Something(a: String, b: String, c: String)

  def getA(id: Int): Option[String] = if (id % 2 == 0) Some("A") else None

  def getB(id: Int): Option[String] = if (id % 2 == 1) Some("B") else None

  def getC(id: Int): Option[String] = if (id % 2 == 0) Some("C") else None

  def main(args: Array[String]): Unit = {

    val something = Something("Aaa", "Bbb", "Ccc")
    val variant = 1

    def update(potentiallyFresherValue: Option[String]) =
      State.modify[Something](s => s.copy(a = s.a + "_", b = s.b + "!", c = s.c + ":"))

    val program = for {
      _   <- update(getA(variant))
      _   <- update(getB(variant))
      res <- update(getC(variant))
    } yield res

    println(program.run(something).value)

  }

}
