package io.underscore.advanced

object IdentityMonad {

  def main(args: Array[String]): Unit = {

    import cats.Id
    def pure[A](value: A): Id[A] = value

    def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)

    def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = map(initial)(func)

    println(pure(123))
    println(map(123)(_ - 111))
    println(flatMap(123)(n => pure(n - 100)))
  }

}
