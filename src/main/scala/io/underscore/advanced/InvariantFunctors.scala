package io.underscore.advanced

object InvariantFunctors {

  trait Codec[A] {

    def encode(value: A): String

    def decode(value: String): Option[A]

    def imap[B](dec: A => B, enc: B => A): Codec[B] = {
      val self = this
      new Codec[B] {
        override def encode(value: B): String = self.encode(enc(value))

        override def decode(value: String): Option[B] = self.decode(value).map(dec)
      }
    }
  }

  def main(args: Array[String]): Unit = {

    case class Box[A](value: A)

    implicit val intCodec = new Codec[Int] {
      def encode(value: Int): String = value.toString

      def decode(value: String): Option[Int] = scala.util.Try(value.toInt).toOption
    }

    def encode[A](value: A)(implicit c: Codec[A]): String = c.encode(value)

    def decode[A](value: String)(implicit c: Codec[A]): Option[A] = c.decode(value)

    implicit def boxCodec[A](implicit c: Codec[A]): Codec[Box[A]] = c.imap[Box[A]](Box.apply, _.value)

    println(encode(Box(123)))
    println(decode[Box[Int]]("123"))
  }

}
