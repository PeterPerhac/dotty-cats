import Stream._

object StreamUnfolds extends App {

  def unfold[A, B](start: B)(f: B => Option[(A,B)]): Stream[A] = f(start) match {
    case Some((elem, next)) => elem #:: unfold(next)(f)
    case None => empty
  }

  unfold((0,1)){case (m,n) => Some((m, (n, m+n))) }.take(10).toList.foreach(println)
  println("---")


}
