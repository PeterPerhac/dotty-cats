package traversals

import cats.data.OptionT
import cats.instances.{FutureInstances, ListInstances}
import cats.syntax.TraverseSyntax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

object Traversal extends TraverseSyntax with FutureInstances with ListInstances {

  final case class PostCode(pc: String)

  def postCode(pc: String) = OptionT.some[Future, PostCode](PostCode(pc))
  val noPostCode = OptionT.none[Future, PostCode]

  def main(args: Array[String]): Unit = {

    val listOfOptionTs: List[OptionT[Future, PostCode]] =
      List(
        postCode("BN3 1JU"),
        noPostCode,
        postCode("BN3 1JU"),
        postCode("BN5 5FX")
      )

    val futureListOfOptions: Future[List[Option[PostCode]]] =
      listOfOptionTs.traverse(_.value)

    val onlyUniques: Future[List[PostCode]] =
      futureListOfOptions.map(_.flatten.distinct)


    Await.result(futureListOfOptions map println, Inf)
    Await.result(onlyUniques map println, Inf)
  }

}
