package parallelFutures

import cats.Applicative

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

object ParallelVsSerialExecution {

  def findFruit(fruit: String) = Future {
    println(s"${System.currentTimeMillis()} looking for fruit")
    Thread.sleep(1000)
    s"I found your $fruit"
  }

  def sequentialFuturesWithMonads(): Future[Unit] =
    for {
      a <- findFruit("apple")
      b <- findFruit("banana")
      c <- findFruit("cherry")
    } yield Seq(a, b, c).foreach(println)


  def parallelFuturesBasic(): Future[Unit] = {
    val fa = findFruit("apple")
    val fb = findFruit("banana")
    val fc = findFruit("cherry")

    for {
      a <- fa
      b <- fb
      c <- fc
    } yield Seq(a, b, c).foreach(println)
  }

  def parallelFuturesWithTraverse()(implicit f: Applicative[Future]): Future[Unit] = {
    import cats.instances.list._
    import cats.syntax.traverse._
    List("apple", "banana", "cherry") traverse findFruit map printer
  }

  def parallelFuturesWithSequence()(implicit f: Applicative[Future]): Future[Unit] = {
    import cats.instances.list._
    import cats.syntax.traverse._
    List(findFruit("apple"), findFruit("banana"), findFruit("cherry")).sequence map printer
  }

  def parallelFuturesWithCartesians()(implicit f: Applicative[Future]): Future[Unit] = {
    import cats.syntax.cartesian._
    findFruit("apple") |@| findFruit("banana") |@| findFruit("cherry") map tuplePrinter
  }

  def main(args: Array[String]): Unit = {
    import cats.instances.future._
    import cats.syntax.applicative._
    val program = for {
      _ <- sequentialFuturesWithMonads()
      _ <- println("\n===\n").pure
      _ <- parallelFuturesBasic()
      _ <- println("\n===\n").pure
      _ <- parallelFuturesWithTraverse()
      _ <- println("\n===\n").pure
      _ <- parallelFuturesWithSequence()
      _ <- println("\n===\n").pure
      _ <- parallelFuturesWithCartesians()
    } yield println("\n===\n")

    Await.result(program, Inf)
  }

  private def printer: (List[String]) => Unit = _.foreach(println)

  private def tuplePrinter: (String, String, String) => Unit = {
    case res@_ => res.productIterator.foreach(println)
  }

}

