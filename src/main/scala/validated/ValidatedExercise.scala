package validated

import cats.data.Validated

final case class User(name:String, age:Int)

object ValidatedExercise {

  import cats.syntax.either._
  import cats.syntax.cartesian._
  import cats.instances.list._
  import cats.instances.either._

  type FormData = Map[String, String]
  type ErrorsOr[A] = Either[List[String], A]
  type AllErrorsOr[A] = Validated[List[String], A]

  // implicit class BindOps[F[_]](left: F)(implicit F:FlatMap[F]) {
  //   def >>= [A,B] (fu: A => F[B]): F[B] = F.flatMap(left)(fu)
  // }

  implicit class BindOps[T](left: ErrorsOr[T]) {
    def >>= [B] (fu: T => ErrorsOr[B]): ErrorsOr[B] = left.flatMap(fu)
  }

  def getValue(key:String)(map:FormData):ErrorsOr[String] = map.get(key).toRight(List(s"no data found for property $key"))

  def readName(data:FormData) = getValue("name")(data) >>= (nonBlank("name"))
  def readAge(data:FormData)  =  getValue("age")(data) >>= (nonBlank("age")) >>= (parseInt("age")) >>= (nonNegative("age"))

  def nonBlank(property: String)(data: String): ErrorsOr[String] =
    Right(data).ensure(List(s"$property cannot be blank"))(_.nonEmpty)

  def nonNegative(property: String)(data: Int): ErrorsOr[Int] =
    Right(data).ensure(List(s"$property must be non-negative"))(_ >= 0)

  def parseInt(property:String)(data:String):ErrorsOr[Int] =
    Either.catchOnly[NumberFormatException](data.toInt).leftMap(_ => List(s"$property must be a valid integer"))

  def parseUser(data: FormData): AllErrorsOr[User] =
    readName(data).toValidated |@| readAge(data).toValidated map User

  def main(args: Array[String]):Unit = {
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
