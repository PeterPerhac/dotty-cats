import java.util.concurrent.atomic.AtomicBoolean

import scala.concurrent.Future


object SequenceRequests {

  import scala.concurrent.ExecutionContext.Implicits.global

  def goodPlaceToLookForIt(foo: String): Future[Option[String]] = Future {
    println("yes, you'll find what you're looking for here!")
    Thread.sleep(1000)
    Option(s"here it is: $foo ! enjoy!")
  }

  def fromS4L(foo: String): Future[Option[String]] = Future {
    println("calling s4l")
    Thread.sleep(1000)
    // Future(Option(s"Save 4 Later found: $foo"))
    None
  }

  def fromBackend(foo: String): Future[Option[String]] =  Future {
    println("calling backend")
    Thread.sleep(1000)
    // Future(Option(s"Backend found: $foo"))
    None
  }

  def fromSomeOtherPlace(foo: String): Future[Option[String]] = Future {
    println("calling another dimension")
    Thread.sleep(1000)
    // Future(Option(s"$foo was found in another dimension"))
    None
  }

  def noNotHere(foo: String): Future[Option[String]] = Future {
    println("doing nothing, you won't find me here")
    Thread.sleep(1000)
    None
  }


  def returnFirstSuccess[A,R](funs: List[(A)=>Future[Option[R]]], arg: A): Future[Option[R]] = funs match {
    case Nil => Future.successful(None)
    case f::tail => f(arg) flatMap {
      case Some(str) => Future.successful(Some(str))
      case None => returnFirstSuccess(tail, arg)
    }
  }

  def main(args: Array[String]): Unit = {
    val arg = "found me!"
    val functions = List.fill(10)(List(noNotHere _, fromS4L _, fromBackend _, fromSomeOtherPlace _)).flatten.updated(20, goodPlaceToLookForIt _)
    println("I will search in this many places:")
    functions foreach println
    val busy = new AtomicBoolean(true)
    returnFirstSuccess(functions, arg) foreach { os  =>
      println(os.getOrElse("The search was futile."))
      busy.compareAndSet(true, false)
    }
    while (busy.get()) {
      Thread.sleep(300)
    }
  }

}
