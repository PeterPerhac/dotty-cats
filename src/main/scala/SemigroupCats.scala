import cats.instances.all._
import cats.syntax.semigroup._
import org.scalatest.Matchers


object SemigroupCats extends Matchers {

  def main(args: Array[String]): Unit = {
    1 |+| 2 shouldBe 3
    List(1, 2, 3) |+| List(4, 5, 6) shouldBe List(1, 2, 3, 4, 5, 6)
    Option(1) |+| Option(2) shouldBe Some(3)
    Option(1) |+| None shouldBe Some(1)
    ({ (x: Int) ⇒ x + 1 } |+| { (x: Int) ⇒ x * 10 }) (6) shouldBe 67
    (Map("foo" → Map("bar" → 5)) |+| Map("foo" → Map("bar" → 6))).get("foo") shouldBe Some(Map("bar" -> 11))
  }

}
