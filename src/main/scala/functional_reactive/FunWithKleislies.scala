package functional_reactive

import cats.data.Kleisli

final case class Person(name: String, age: Option[Int])

trait Repository[Id, Entity] {

  def findById(id: Id): Option[Entity]

  def findFirst(predicate: Entity => Boolean): Option[Entity]
}

final class MyRepository extends Repository[Int, Person] {

  import cats.instances.option._
  import cats.syntax.applicative._

  val people: Vector[Person] = Vector(
    Person("Peter", Some(54)),
    Person("Peter", Some(32)),
    Person("Peter Emil", Some(5)),
    Person("Barbora", None)
  )

  override def findById(id: Int): Option[Person] = id match {
    case n if n < people.size => people(n).pure
    case _ => Option.empty
  }

  override def findFirst(predicate: Person => Boolean): Option[Person] =
    people.find(predicate)

}


trait FunService[Id, Repository, Person] {

  def getPerson(id: Id): Kleisli[Option, Repository, Person]

  def getPersonByAge(age: Int): Kleisli[Option, Repository, Person]

}


final class FunServiceInterpreter extends FunService[Int, MyRepository, Person] {

  import cats.instances.option._


  override def getPerson(id: Int): Kleisli[Option, MyRepository, Person] = Kleisli(_.findById(id))

  override def getPersonByAge(age: Int): Kleisli[Option, MyRepository, Person] = Kleisli(_.findFirst(_.age.exists(_ == age)))

  def formatPerson(id: Int): Kleisli[Option, MyRepository, String] = for {
    p <- getPerson(id)
    a <- getPersonByAge(p.age.getOrElse(0))
  } yield {
    s"${p.name} is $a years old"
  }

}

object FunWithKleislies {

  def main(args: Array[String]): Unit = {

    println(new FunServiceInterpreter().formatPerson(1) run new MyRepository)


  }

}
