import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import cats.instances.FutureInstances
import cats.data.OptionT

object OptionTValue extends FutureInstances {

  def callToAction():Future[String] = OptionT.none[Future, String].value.map(_ => "Something")
  def callToAction2():Future[String] = OptionT.some[Future, String]("Chuj").cata("Foobar", _ => "Another thing")

  def main(args: Array[String]): Unit = {
    val futureRedirect = callToAction2()
    Await.result(futureRedirect map println, Inf)
  }

}

