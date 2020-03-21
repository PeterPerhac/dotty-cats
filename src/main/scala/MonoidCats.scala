import org.scalatest.matchers.should.Matchers

object MonoidCats extends Matchers {

  import cats._
  import cats.implicits._

  def main(args: Array[String]): Unit = {
    val l = List(1, 2, 3, 4, 5)
    l.foldMap(identity) shouldBe 15
    l.foldMap(i => i.toString) shouldBe "12345"
    l.foldMap(i => (i, i.toString)) should be((15, "12345"))

    Monoid[Map[String, Int]].combineAll(List(Map("a" -> 1, "b" -> 2), Map("a" -> 3))) shouldBe Map("a" -> 4, "b" -> 2)
    Monoid[Map[String, Int]].combineAll(List()) shouldBe Map()
  }

}
