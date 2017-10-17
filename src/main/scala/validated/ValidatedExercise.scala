package validated

import cats.data.Validated
import cats.implicits._
import cats.syntax.ApplySyntax

import scala.language.higherKinds

final case class User(name: String, age: Int)

object ValidatedExercise extends ApplySyntax{

  import cats.syntax.either._

  type FormData = Map[String, String]
  type ErrorsOr[A] = Either[List[String], A]
  type AllErrorsOr[A] = Validated[List[String], A]

  implicit class BindOps[T](left: ErrorsOr[T]) {
    def >>=[B](fu: T => ErrorsOr[B]): ErrorsOr[B] = left.flatMap(fu)
  }

  def getValue(key: String)(map: FormData): ErrorsOr[String] = map.get(key).toRight(List(s"no data found for property $key"))

  def readName(data: FormData): ErrorsOr[String] = getValue("name")(data) >>= nonBlank("name")

  def readAge(data: FormData): ErrorsOr[Int] = getValue("age")(data) >>= nonBlank("age") >>= parseInt("age") >>= nonNegative("age")

  def nonBlank(property: String)(data: String): ErrorsOr[String] =
    Either.right[List[String], String](data).ensure(List(s"$property cannot be blank"))(_.nonEmpty)

  def nonNegative(property: String)(data: Int): ErrorsOr[Int] =
    Either.right[List[String], Int](data).ensure(List(s"$property must be non-negative"))(_ >= 0)

  def parseInt(property: String)(data: String): ErrorsOr[Int] =
    Either.catchOnly[NumberFormatException](data.toInt).leftMap(_ => List(s"$property must be a valid integer"))

  def parseUser(data: FormData): AllErrorsOr[User] =
    (readName(data).toValidated, readAge(data).toValidated).mapN(User)

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
