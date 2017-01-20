object TraversableCats extends org.scalatest.Matchers {

  def main(args: Array[String]): Unit = {
    import cats.implicits._
    List(Option(1), Option(2), Option(3)).sequence_ should be(Some(()))
    List(Option(1), None, Option(3)).sequence_ should be(None)
  }

}
