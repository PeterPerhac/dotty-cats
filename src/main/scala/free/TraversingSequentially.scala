package free

import cats.free.Free
import cats.free.Free.liftF
import cats.implicits._
import cats.{Monad, Traverse, ~>}

import scala.collection.mutable
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.language.higherKinds

object TraversingSequentially {

  import cats.instances.future._

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit class RichTraverse[F[_], A](as: F[A]) {

    def sequentialTraverse[B, M[_] : Monad](f: A => M[B])(implicit T: Traverse[F]): M[F[B]] = {
      case class LazyFunction[X](a: A, f: A => M[X]) {
        def apply: M[X] = f(a)
      }

      type FreeLazyFunction[X] = Free[LazyFunction, X]

      def lift[X](func: A => M[X])(a: A): FreeLazyFunction[X] =
        liftF(LazyFunction(a, func))

      val program: FreeLazyFunction[F[B]] = T.traverse(as)(lift(f))

      program.foldMap(new (LazyFunction ~> M) {
        override def apply[X](o: LazyFunction[X]): M[X] = o.apply
      })
    }
  }

  def main(args: Array[String]): Unit = {
    val x = List(5, 4, 3, 2, 1)
    var results = new mutable.MutableList[Int]()

    // If we were executing in parallel, this function would reverse the list `x` into `results`
    def f(i: Int): Future[String] = {
      println(s"time for a nap: $i")
      Thread.sleep(i * 500)
      results = results :+ i
      Future.successful(i.toString)
    }

    println(Await.result(x.sequentialTraverse(f), Inf))
  }
}
