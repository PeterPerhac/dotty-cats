import cats._, cats.instances.all._, cats.syntax.semigroup._
import cats._
import cats.instances.all._
import cats.syntax.semigroup._

object SemigroupCats {

  def main(args: Array[String]): Unit = {
    println(List(1, 2, 3) |+| List(4, 5, 6))
  }

}
