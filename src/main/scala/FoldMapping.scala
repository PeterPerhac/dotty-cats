object FoldMapping
    extends org.scalatest.matchers.should.Matchers with cats.instances.AllInstances with cats.syntax.FoldableSyntax {

  def main(args: Array[String]): Unit = {
    val l = List(1, 2, 3, 4, 5)
    l.foldMap(identity) shouldBe 15
    l.foldMap(i => i.toString) shouldBe "12345"
    l.foldMap(i => (i, i.toString)) should be((15, "12345"))
  }

}
