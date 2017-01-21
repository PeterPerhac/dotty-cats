import cats.Traverse

object TraversableCats extends org.scalatest.Matchers {

  def main(args: Array[String]): Unit = {
    import cats.implicits._
    Traverse[List].sequence_(List(Option(1), Option(2), Option(3))) should be(Some(List(1,2,3)))
    List(Option(1), None, Option(3)).sequence_ should be(None)
  }

}
