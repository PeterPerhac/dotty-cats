package validated

import scala.concurrent.{Await, Future}
import cats.syntax.traverse._
import cats.instances.future._
import cats.instances.list._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object HelpMeWithHaskell {

  def main(args: Array[String]): Unit = {
    val f: Int => Future[Unit] = n => Future.successful(println(n))
    val foos = (1 to 100).toList
    val it: Future[Unit] = foos.traverse(f).map(_ => ())
    Await.result(it, Duration("5s"))
  }

}
