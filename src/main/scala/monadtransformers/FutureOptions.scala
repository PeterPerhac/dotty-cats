package monadtransformers

import cats.data.OptionT

import scala.concurrent.Future

object FutureOptions {

  import scala.concurrent.ExecutionContext.Implicits.global
  import cats.instances.future._

  def foa[A](a: A): Future[Option[A]] = Future.successful(Option(a))
  def oa[A](a: A): Option[A] = Option(a)
  def fa[A](a: A): Future[A] = Future.successful(a)

  def main(args: Array[String]): Unit = {
    val hello = foa("Hello")
    val comma = fa(", ")
    val world = foa("world")
    val exclamation = oa("!")

    for {
      greeting  <- OptionT(hello)
      separator <- OptionT.liftF[Future, String](comma)
      subject   <- OptionT(world)
      ending    <- OptionT.fromOption(exclamation)
    } yield {}

  }

}
