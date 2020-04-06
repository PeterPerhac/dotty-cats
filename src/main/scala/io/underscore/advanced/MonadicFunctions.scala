package io.underscore.advanced

object MonadicFunctions {

  def main(args: Array[String]): Unit = {
    import cats.instances.function._
    import cats.syntax.flatMap._
    import cats.syntax.functor._

//    val addStuff = for {
//      a <- (_: String).trim.toInt
//      a2 <- (_: String).trim.toDouble
//      b <- (_: String) * 2
//      c <- (_: String).trim.reverse
//    } yield a + a2 + b + c
  }

}
