package validated

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.syntax.ApplySyntax

import scala.util.Try

final case class User2(name: String, age: Int)

object ValidatedExercise2 extends ApplySyntax {

  type FormData = Map[String, String]
  type Valid[T] = ValidatedNel[String, T]

  implicit class syntax[T](val a: Valid[T]) extends AnyVal {
    def >>[B](b: T => Valid[B]): Valid[B] = a andThen b
  }

  def extract(key: String)(map: FormData): Valid[String] =
    Validated.fromOption(map.get(key), s"$key is missing").toValidatedNel

  def validateName(data: FormData): Valid[String] =
    extract("name")(data) >> nonBlank("name")

  def validateAge(data: FormData): Valid[Int] =
    extract("age")(data) >> nonBlank("age") >> parseInt("age") >> nonNegative("age")

  def nonBlank(x: String)(data: String): Valid[String] =
    Valid(data).ensure(s"$x cannot be blank")(_.nonEmpty).toValidatedNel

  def nonNegative(x: String)(data: Int): Valid[Int] =
    Valid(data).ensure(s"$x must be positive")(_ > 0).toValidatedNel

  def parseInt(x: String)(data: String): Valid[Int] =
    Validated.fromTry(Try(data.toInt)).leftMap(_ => s"$x must be a valid integer").toValidatedNel

  def parseUser(data: FormData): Valid[User2] =
    (validateName(data), validateAge(data)).mapN(User2)

  def main(args: Array[String]): Unit = {
    val data = Map("name" -> "Peter", "age" -> "32")
    val badData1 = Map("name" -> "Peter", "age" -> "-12")
    val badData2 = Map("naam" -> "Peter", "age" -> "-12")
    val badData3 = Map("naam" -> "Peter", "age" -> "foobar")
    val badData4 = Map("name" -> "Peter", "age" -> "foobar")
    val badData5 = Map("name" -> "", "age" -> "")
    println(s" parsed user: ${parseUser(data)}")
    println(s" parsed user: ${parseUser(badData1)}")
    println(s" parsed user: ${parseUser(badData2)}")
    println(s" parsed user: ${parseUser(badData3)}")
    println(s" parsed user: ${parseUser(badData4)}")
    println(s" parsed user: ${parseUser(badData5)}")
  }

}
