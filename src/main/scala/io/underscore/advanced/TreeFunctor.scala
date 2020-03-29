package io.underscore.advanced

import cats.Functor

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]

object TreeFunctor {

  def main(args: Array[String]): Unit = {

    implicit val treeFunctor = new Functor[Tree] {
      override def map[A, B](fa: Tree[A])(f: (A) => B): Tree[B] = fa match {
        case Branch(l, r) => Branch(map(l)(f), map(r)(f))
        case Leaf(value)  => Leaf(f(value))
      }
    }

    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)

    def leaf[A](value: A): Tree[A] = Leaf(value)

    import cats.syntax.functor._

    println(branch(leaf(1), branch(leaf(2), leaf(3))).map(i => s"---$i---"))
  }

}
