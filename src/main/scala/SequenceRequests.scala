import java.util.concurrent.CountDownLatch

import scala.concurrent.Future


object SequenceRequests {

  import scala.concurrent.ExecutionContext.Implicits.global

  private class ServiceCall[-A, +R](f: (A) => Future[Option[R]], name: String = "") extends Function1[A, Future[Option[R]]] {

    override def apply(arg: A): Future[Option[R]] = f(arg)

    override def toString() = s"Call to service $name"
  }

  private object ServiceCall {
    def apply(f: (String) => Future[Option[String]], name: String = "") = new ServiceCall(f, name)
  }

  def specialPlace(foo: String): Future[Option[String]] = Future(Some(s"You found $foo!"))

  def placeOne(foo: String): Future[Option[String]] = Future {
    println("calling one place")
    Thread.sleep(1000)
    None
  }

  def placeTwo(foo: String): Future[Option[String]] = Future {
    println("calling another")
    Thread.sleep(1000)
    None
  }

  val notHere: (String) => Future[Option[String]] = _ => {
    println("nope")
    Future.successful(None)
  }

  def returnFirstSuccess[A, R](funs: List[(A) => Future[Option[R]]], arg: A): Future[Option[R]] = funs match {
    case Nil => Future.successful(None)
    case f :: tail => f(arg) flatMap {
      case Some(str) => Future.successful(Some(str))
      case None => returnFirstSuccess(tail, arg)
    }
  }

  def main(args: Array[String]): Unit = {
    val arg = "everlasting glory and fame"
    val fus = List.fill(5)(List(ServiceCall(placeOne), ServiceCall(placeTwo), ServiceCall(notHere))).flatten
    val functions = fus.updated(7, ServiceCall(specialPlace, name = "Happy"))

    println("I will search in this many places:")
    functions foreach println

    println("====")

    val latch = new CountDownLatch(1)
    returnFirstSuccess(functions, arg) foreach { os =>
      println(os.getOrElse("The search was futile."))
      latch.countDown()
    }

    latch.await()
  }

}
