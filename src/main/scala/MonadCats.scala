import cats.Monad
import cats.data.OptionT
import org.scalatest.Matchers

object MonadCats extends Matchers {

  import cats.implicits._

  def main(args: Array[String]): Unit = {
    Monad[Option].ifM(Option(true))(Option("truthy"), Option("falsy")) should be(Some("truthy"))
    Monad[List].ifM(List(true, false, true))(List(1, 2), List(3, 4)) should be(List(1, 2, 3, 4, 1, 2))
    OptionT.pure[List, Int](42).value shouldBe List(Some(42))

    val optionT = OptionT(List(Option(1), Option(2), None, Option(3), None))
    optionT.fold("")(_.toString).mkString shouldBe "123"
    optionT.cata("", _.toString).mkString shouldBe "123" //catamorphism of fold (single parameter list)

  }

}
