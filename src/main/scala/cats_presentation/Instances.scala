package cats_presentation

object Instances {

  def main(args: Array[String]): Unit = {
    import cats.syntax.applicative._
    val boxedNumber = 42.pure

    import cats.instances.list._
    val boxedNumber2 = 42.pure

//    import cats.instances.option._
//    val boxedNumber3 = 42.pure

  }

}
