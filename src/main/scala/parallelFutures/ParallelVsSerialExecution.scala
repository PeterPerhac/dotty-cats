package parallelFutures

import cats.Traverse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

object ParallelVsSerialExecution {

  private def printer: (List[String]) => Unit = _.foreach(println)

  private def argumentPrinter: (String, String, String) => Unit = {
    case res@_ => res.productIterator.foreach(println)
  }

  def findFruit(fruit: String) = Future {
    println(s"${System.currentTimeMillis()} looking for yummy fruit")
    Thread.sleep(1000)
    s"I found your $fruit"
  }

  def sequentialFuturesWithMonads(): Future[Unit] = {
    for {
      a <- findFruit("apple")
      b <- findFruit("banana")
      c <- findFruit("cherry")
    } yield {
      Seq(a, b, c).foreach(println)
    }
  }

  def parallelFuturesWithTraverse(): Future[Unit] = {
    import cats.instances.future._
    import cats.instances.list._
    import cats.syntax.traverse._
    List("apple", "banana", "cherry") traverse findFruit map printer
  }

  def parallelFuturesWithSequence(): Future[Unit] = {
    import cats.instances.future._
    import cats.instances.list._
    Traverse[List] sequence List(findFruit("apple"), findFruit("banana"), findFruit("cherry")) map printer
  }


  def parallelFuturesWithCartesians(): Future[Unit] = {
    import cats.instances.future._
    import cats.syntax.cartesian._
    findFruit("apple") |@| findFruit("banana") |@| findFruit("cherry") map argumentPrinter
  }

  def main(args: Array[String]): Unit = {
    Await.result(sequentialFuturesWithMonads(), Inf)
    println("\n===\n")
    Await.result(parallelFuturesWithTraverse(), Inf)
    println("\n===\n")
    Await.result(parallelFuturesWithSequence(), Inf)
    println("\n===\n")
    Await.result(parallelFuturesWithCartesians(), Inf)
  }

}
