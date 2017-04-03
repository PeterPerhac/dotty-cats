package io.underscore.advanced

import cats.data.{NonEmptyVector, Validated}

final case class ValidationError(message: String, arguments: Any*)

final case class Pet(name: String, age: Option[Int] = None)


object ValidatedStuff {

  import cats.instances.option._
  import cats.syntax.applicative._
  import cats.syntax.cartesian._
  import cats.syntax.validated._

  type Checked[T] = Validated[NonEmptyVector[ValidationError], T]

  def checkName(pet: Pet): Checked[Pet] = pet.name match {
    case null => ValidationError("Pet's name can't be null", pet).pure[NonEmptyVector].invalid[Pet]
    case s: String if s.trim.isEmpty => ValidationError("blank pet's name").pure[NonEmptyVector].invalid[Pet]
    case s: String if s.length == 1 => ValidationError("Pet's name can't be single character").pure[NonEmptyVector].invalid[Pet]
    case _ => pet.valid
  }

  def checkAge(pet: Pet): Checked[Pet] = pet.age match {
    case Some(age) if age < 0 || age > 50 => ValidationError("Pet's age must be >=0 and <=50", age).pure[NonEmptyVector].invalid[Pet]
    case _ => pet.valid
  }

  def main(args: Array[String]): Unit = {

    val pet = Pet("x", age = 99.pure)
    val pet2 = Pet("x", age = 20.pure)
    val pet3 = Pet("foo", age = 20.pure)

    (checkName(pet) |@| checkAge(pet)) map {case _@_=> pet} fold (println, println)
    (checkName(pet2) |@| checkAge(pet2)) map {case _@_=> pet2} fold (println, println)
    (checkName(pet3) |@| checkAge(pet3)) map {case _@_=> pet3} fold (println, println)
  }


}
