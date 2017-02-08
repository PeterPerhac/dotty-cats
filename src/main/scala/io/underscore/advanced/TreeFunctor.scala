package io.underscore.advanced

import cats.Functor

sealed trait Tree[+A]

final case class Branch[A](left: Tree[A], right: Tree[A])
  extends Tree[A]

final case class Leaf[A](value: A) extends Tree[A]


object TreeFunctor {


  def main(args: Array[String]): Unit = {
    implicit val treeFunctor = new Functor[Tree] {
      override def map[A, B](fa: Tree[A])(f: (A) => B): Tree[B] = fa match {
        case Branch(l, r) => Branch(map(l)(f), map(r)(f))
        case Leaf(value) => Leaf(f(value))
      }
    }
    val aTree: Tree[Int] = Branch(Leaf(1), Branch(Leaf(2), Leaf(3)))
    import cats.syntax.functor._
    val bTree: Tree[String] = aTree.map(i => s"--$i--")
    println(bTree)
  }

}
