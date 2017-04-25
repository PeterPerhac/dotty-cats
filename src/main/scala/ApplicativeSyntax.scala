
object ApplicativeSyntax {

  def main(args: Array[String]): Unit = {
    import cats.syntax.applicative._
    import cats.instances.list._

    val foo = true.pure
    println(foo)
  }
}
