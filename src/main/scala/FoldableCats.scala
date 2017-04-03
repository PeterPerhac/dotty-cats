import cats.Foldable
import org.scalatest.Matchers

object FoldableCats extends Matchers {


  def main(args: Array[String]): Unit = {
    import cats.implicits._

    Foldable[List].foldK(List(List(1, 2), List(3, 4, 5))) should be(List(1, 2, 3, 4, 5))
    Foldable[List].foldK(List(None, Option("two"), Option("three"))) should be(Some("two"))

    def parseInt(s: String): Option[Int] =
      Either.catchOnly[NumberFormatException](s.toInt).toOption

    Foldable[List].traverse_(List("1", "2", "3"))(parseInt) should be(Some)
    Foldable[List].traverse_(List("a", "b", "c"))(parseInt) should be(None)

  }

}
