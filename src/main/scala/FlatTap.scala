import cats.Apply

import scala.concurrent.{Await, Future}
import cats.syntax.flatMap._
import cats.instances.future._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf


object FlatTap {

  val f: String => Future[Int] = s => {
    println("executing side-effect")
    Future.successful(s.length)
  }

  val f1 = Future.successful("Hello World").flatMap(a => f(a).map(_ => a))
  val f2 = Future.successful("Hello World").flatTap(f)

  def main(args: Array[String]): Unit = {
    val program = Apply[Future].map2(f1, f2)(_ + _.reverse)
    println(Await.result(program, Inf))
  }

}
