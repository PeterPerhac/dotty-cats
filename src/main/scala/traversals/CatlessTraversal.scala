package traversals

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

object CatlessTraversal {

  final case class PostCode(pc: String)

  def postCode(pc: String) = Future.successful(Some(PostCode(pc)))
  val noPostCode = Future.successful(Option.empty[PostCode])

  def main(args: Array[String]): Unit = {

    val listOfFutureOptions: List[Future[Option[PostCode]]] =
      List(
        postCode("BN3 1JU"),
        noPostCode,
        postCode("BN3 1JU"),
        postCode("BN5 5FX")
      )

    val futureListOfOptions: Future[List[Option[PostCode]]] =
      Future.sequence(listOfFutureOptions)

    val onlyUniques: Future[List[PostCode]] =
      Future.sequence(listOfFutureOptions).map(_.flatten.distinct)

    Await.result(futureListOfOptions map println, Inf)
    Await.result(onlyUniques map println, Inf)
  }

}
