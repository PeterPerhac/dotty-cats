package validated

import cats.data.Validated
import cats.instances.ListInstances
import cats.syntax.{ApplySyntax, EitherSyntax}
import scala.{Either => Or}

final case class User(name: String, age: Int)

object ValidatedExercise extends ApplySyntax with EitherSyntax with ListInstances {

  type FormData = Map[String, String]
  type Errors = List[String]

  implicit class BindOps[T](left: Errors Or T) {
    def >>=[B](fu: T => Errors Or B): Errors Or B = left.flatMap(fu)
  }

  def getValue(key: String)(map: FormData): Errors Or String =
    map.get(key).toRight(List(s"no data found for property $key"))

  def readName(data: FormData): Errors Or String = getValue("name")(data) >>= nonBlank("name")

  def readAge(data: FormData): Errors Or Int =
    getValue("age")(data) >>= nonBlank("age") >>= parseInt("age") >>= nonNegative("age")

  def nonBlank(property: String)(data: String): Errors Or String =
    Either.right[Errors, String](data).ensure(List(s"$property cannot be blank"))(_.nonEmpty)

  def nonNegative(property: String)(data: Int): Errors Or Int =
    Either.right[Errors, Int](data).ensure(List(s"$property must be non-negative"))(_ >= 0)

  def parseInt(property: String)(data: String): Errors Or Int =
    Either.catchOnly[NumberFormatException](data.toInt).leftMap(_ => List(s"$property must be a valid integer"))

  def parseUser(data: FormData): Validated[Errors, User] =
    (readName(data).toValidated, readAge(data).toValidated).mapN(User)

  def main(args: Array[String]): Unit = {
    val data = Map("name"     -> "Peter", "age" -> "32")
    val badData1 = Map("name" -> "Peter", "age" -> "-12")
    val badData2 = Map("naam" -> "Peter", "age" -> "-12")
    val badData3 = Map("naam" -> "Peter", "age" -> "foobar")
    val badData4 = Map("name" -> "Peter", "age" -> "foobar")
    val badData5 = Map("name" -> "", "age"      -> "")
    println(s" parsed user: ${parseUser(data)}")
    println(s" parsed user: ${parseUser(badData1)}")
    println(s" parsed user: ${parseUser(badData2)}")
    println(s" parsed user: ${parseUser(badData3)}")
    println(s" parsed user: ${parseUser(badData4)}")
    println(s" parsed user: ${parseUser(badData5)}")
  }

}
