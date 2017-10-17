package parallelFutures

import cats.instances.FutureInstances
import cats.syntax.TupleCartesianSyntax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}

class Pid(val pid:String) extends AnyVal

final case class Profile(pid: Pid, name: String, age: Int)

object SequentialAndParallelFutures extends TupleCartesianSyntax with FutureInstances {

  def main(args: Array[String]): Unit = {
//    val program = for {
//      id <- getProfileId("reteP")
//      p <- getProfile(id)
//      (prefs, balance) <- (getPreferences(p.pid), getBalance(p.pid)).tupled
//    } yield println(s"${System.currentTimeMillis()} ${p.name}'s balance is $balance. Preferences: $prefs")
//    Await.result(program, Inf)
  }

//  def getProfileId(input: String): Future[Pid] = Future {
//    println(s"${System.currentTimeMillis()} getProfileId")
//    Thread.sleep(100)
//    new Pid(input)
//  }
//
//  def getProfile(pid: Pid): Future[Profile] = Future {
//    println(s"${System.currentTimeMillis()} getProfile")
//    Thread.sleep(100)
////    Profile(, pid.pid.reverse, pid.pid.length)
//  }
//
//  def getPreferences(pid: String): Future[String] = Future {
//    println(s"${System.currentTimeMillis()} getPreferences")
//    Thread.sleep(100)
//    pid
//  }
//
//  def getBalance(pid: String): Future[Long] = Future {
//    println(s"${System.currentTimeMillis()} getBalance")
//    Thread.sleep(100)
//    System.currentTimeMillis
//  }

}