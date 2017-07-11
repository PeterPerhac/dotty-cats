package validated

import cats.data.Validated

final case class User(name:String, age:Int)
object ValidatedExercise extends App {

  import cats.syntax.either._

  type FormData = Map[String, String]
  type ErrorsOr[A] = Either[List[String], A]
  type AllErrorsOr[A] = Validated[List[String], A]

  def getValue(key:String)(map:FormData):ErrorsOr[String] = map.get(key).toRight(List(s"no data found for property $key"))

  val readName = getValue("name") _
  val readAge = getValue("age")(_:FormData).flatMap(parseInt("age"))

  def parseInt(name:String)(data:String):ErrorsOr[Int] =
    Either.catchOnly[NumberFormatException](data.toInt).leftMap(_ => List(s"$name must be a valid integer"))


  // - thenameandagemustbespecified;
  // - thenamemustnotbeblank;
  // - thetheagemustbeavalidnon-nega veinteger.
}
