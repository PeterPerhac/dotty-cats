package io.underscore.advanced

import cats.Monoid

case class Order(totalCost: Double, quantity: Double)

object Order {

  implicit object OrderMonoid extends Monoid[Order] {
    override def empty: Order = Order(0.0, 0.0)

    override def combine(x: Order, y: Order): Order = Order(x.totalCost + y.totalCost, x.quantity + y.quantity)
  }

}

object AddingThings {

  import cats.Monoid
  import cats.syntax.semigroup._

  def add[A: Monoid](items: List[A]): A = items.foldLeft(Monoid[A].empty)(_ |+| _)

  def main(args: Array[String]): Unit = {
    import cats.instances.int._
    println(add(List(1, 2, 3)))

    import cats.instances.option._
    println(add(List(Some(1), None, Some(2), None, Some(3))))

    println(add(List(Order(10, 20), Order(20, 30), Order(5, 5))))
  }

}
