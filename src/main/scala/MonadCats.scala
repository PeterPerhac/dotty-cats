import cats.Monad
import cats.data.OptionT
import org.scalatest.Matchers

object MonadCats extends Matchers {

  import cats.implicits._

  def main(args: Array[String]): Unit = {
    Monad[Option].ifM(Option(true))(Option("truthy"), Option("falsy")) should be(Some("truthy"))
    Monad[List].ifM(List(true, false, true))(List(1, 2), List(3, 4)) should be(List(1, 2, 3, 4, 1, 2))
    OptionT.pure[List, Int](42).value shouldBe List(Some(42))

    List(1, 2, 3) map OptionT.pure[List, Int] map (_.value) shouldBe List(List(Some(1)), List(Some(2)), List(Some(3)))
  }

}
