import cats.data.OptionT

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object CollectingOptionTs {

  def main(args: Array[String]): Unit = {
    import cats.instances.future._
    import cats.instances.list._
    import cats.syntax.traverse._

    val lots: List[OptionT[Future, Int]] = List(OptionT.some(4), OptionT.none, OptionT.some(2))
    val floints: Future[List[Int]] = lots.traverse(_.value).map(_.flatten)
    println(Await.result(floints, Duration.Inf))
  }

}
