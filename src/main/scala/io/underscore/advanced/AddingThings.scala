package io.underscore.advanced


case class Order(totalCost: Double, quantity: Double)

object AddingThings {

  import cats.Monoid
  import cats.syntax.semigroup._

  def add[A: Monoid](items: List[A]): A = items.foldLeft(Monoid[A].empty)(_ |+| _)

  def main(args: Array[String]): Unit = {
    import cats.instances.int._
    println(add(List(1, 2, 3)))

    import cats.instances.option._
    println(add(List(Some(1), None, Some(2), None, Some(3))))

    implicit val _: Monoid[Order] = new Monoid[Order] {
      def combine(o1: Order, o2: Order) =
        Order(
          o1.totalCost + o2.totalCost,
          o1.quantity + o2.quantity
        )

      def empty = Order(0, 0)
    }

    println(add(List(Order(10, 20), Order(20, 30), Order(5, 5))))
  }

}
