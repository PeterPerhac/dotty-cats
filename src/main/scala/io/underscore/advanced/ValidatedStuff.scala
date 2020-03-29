package io.underscore.advanced

import cats.data.{NonEmptyList, Validated}
import cats.syntax.{ApplySyntax, ValidatedSyntax}

final case class ValidationError(message: String, arguments: Any*)

final case class Cat(name: String, age: Option[Int] = None)

object ValidatedStuff extends ApplySyntax with ValidatedSyntax {

  type V[T] = Validated[ValidationError, T]

  def checkName(name: String): V[String] = name match {
    case s if Option(s).exists(_.trim.isEmpty) => ValidationError("Name must be provided", s).invalid
    case s: String if s.length == 1            => ValidationError("Name can't be single character").invalid
    case s                                     => s.valid
  }

  def checkAge(age: Option[Int]): V[Option[Int]] = age match {
    case Some(n) if n < 0 || n > 50 => ValidationError("Age must be >=0 and <=50", n).invalid
    case n                          => age.valid
  }

  def main(args: Array[String]): Unit = {

    val errorFormatter = (nel: NonEmptyList[ValidationError]) =>
      s"an invalid cat ${nel.map(_.message).toList.mkString("(", ", ", ")")}"
    List(
      ""      -> Some(30),
      "x"     -> Some(20),
      "Mitzi" -> Some(51),
      "x"     -> Some(99),
      ""      -> Some(99),
      "Mitzi" -> Some(20),
      "Mitzi" -> None).foreach {
      case (n, a) =>
        val cat =
          (checkName(n).toValidatedNel, checkAge(a).toValidatedNel).mapN(Cat).fold(errorFormatter, _ => "a valid cat")
        println(s"Inputs $n and $a make $cat")
    }
  }

}
