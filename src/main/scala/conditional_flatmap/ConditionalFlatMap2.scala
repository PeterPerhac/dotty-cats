package conditional_flatmap

import cats.Applicative
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.util.Random.nextInt

object ConditionalFlatMapExample2 extends ConditionalFlatMap {

  import cats.instances.future._

  def iPrint(i: Int) = Future { Thread.sleep(150); println(s"printing: $i") }
  def randomNumber() = Future { Thread.sleep(150);  nextInt(84) }

  def program() = for {
    magicNum <- randomNumber()
    _        <- iPrint(magicNum)
  } yield magicNum

  def program2() = for {
    magicNum <- randomNumber()
    _        <- iPrint(magicNum) onlyIf magicNum > 42
  } yield magicNum

  def main(args: Array[String]): Unit = {
    (1 to 10).foreach( _ => Await.result(program().map(println), Inf))
    println("=====================")
    (1 to 10).foreach( _ => Await.result(program2().map(println), Inf))
  }

}

