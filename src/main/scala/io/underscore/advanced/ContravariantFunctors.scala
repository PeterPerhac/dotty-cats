package io.underscore.advanced


final case class Box[A](value: A)

object ContravariantFunctors {

  implicit val stringPrintable = new Printable[String] {
    def format(value: String): String = "\"" + value + "\""
  }

  implicit val booleanPrintable = new Printable[Boolean] {
    def format(value: Boolean): String = if (value) "yes" else "no"
  }

  trait Printable[A] {

    def format(value: A): String

    def contramap[B](func: B => A): Printable[B] = {
      val self = this
      (value: B) => self.format(func(value))
    }

  }

  def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)


  def main(args: Array[String]): Unit = {

    implicit def boxPrintable[A](implicit p: Printable[A]) = p.contramap[Box[A]](_.value)

    println(format(Box("Hello World")))
    println(format(Box(true)))
//    println(format(Box(123)))

  }


}
