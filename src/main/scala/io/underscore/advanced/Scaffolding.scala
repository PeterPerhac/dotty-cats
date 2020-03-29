package io.underscore.advanced

import cats.Monoid

object Scaffolding {

  def main(args: Array[String]): Unit = {

    def map[A, B](as: List[A])(f: A => B): List[B] = as.foldRight(Nil: List[B])(f(_) :: _)

    def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] = as.foldRight(Nil: List[B])(f(_) ::: _)

    def filter[A](as: List[A])(p: A => Boolean): List[A] =
      as.foldRight(Nil: List[A])((a, b) => if (p(a)) a :: b else b)

    def sum[A](as: List[A])(implicit ma: Monoid[A]) = as.foldRight(ma.empty)(ma.combine)

    import cats.instances.int._

    val ints = List(1, 2, 3, 4, 5)
    println(map(ints)(_.toString + "!"))
    println(flatMap(ints)(a => List(a, a.toString + "!")))
    println(filter(ints)(_ % 2 != 0))
    println(sum(ints))

  }
}
