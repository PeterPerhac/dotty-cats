import cats.data.OptionT
import cats.syntax.ApplicativeSyntax
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object OptionTValue {

  def main(args: Array[String]): Unit = {
    import cats.instances.future._
    val f1 = Future.failed[String](new IllegalArgumentException())
    val ot = OptionT.liftF(f1)
    val f2 = ot.fold("F")(_ => "T") recover {case _ => "F"}
    Await.ready(f2 map println, Inf)
  }

}

