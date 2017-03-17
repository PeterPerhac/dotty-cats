package adts

import play.api.libs.json._

sealed trait Colour { val hex: String }
case object Brown extends Colour { val hex = "#8B4513" }
case object Yellow extends Colour { val hex = "#ffff00" }
case object White extends Colour { val hex = "#ffffff" }
case object Black extends Colour { val hex = "#000000" }

object Colour {
  implicit object ColourFormatter extends Format[Colour] {
    override def writes(o: Colour): JsValue = JsObject(Map("hex" -> JsString(o.hex)))
    override def reads(json: JsValue): JsResult[Colour] = (json \ "hex").as[String] match {
      case Black.hex => JsSuccess(Black)
      case White.hex => JsSuccess(White)
      case Yellow.hex => JsSuccess(Yellow)
      case Brown.hex => JsSuccess(Brown)
      case _ => JsError("unrecognised colour")
    }
  }
}

sealed trait Pet
final case class Cat(name: String, age: Option[Int] = None, `type`: String = "cat") extends Pet
final case class Dog(name: String, age: Option[Int] = None, colour: Colour, `type`: String = "dog") extends Pet

object Pet {

  implicit object PetFormatter extends Format[Pet] {
    override def writes(o: Pet): JsValue = o match {
      case cat: Cat => Json.format[Cat].writes(cat)
      case dog: Dog => Json.format[Dog].writes(dog)
    }

    override def reads(json: JsValue): JsResult[Pet] = (json \ "type").as[String] match {
      case "cat" => Json.format[Cat].reads(json)
      case "dog" => Json.format[Dog].reads(json)
      case _ => JsError("unrecognised pet")
    }
  }

}


object JsonFormatForADT {

  implicit class PetPrinter(pet: Pet) {
    def print(implicit w: Writes[Pet]): Unit = println(w.writes(pet).toString())
  }

  def main(args: Array[String]): Unit = {

    Cat("minnie").print
    Cat("mixie", age = Some(10)).print
    Dog("waldo", colour = Brown).print

    val cat1 = Json.parse("""{"name":"minnie","type":"cat"}""").as[Pet]
    val cat2 = Json.parse("""{"name":"mixie","age":10,"type":"cat"}""").as[Pet]
    val dog = Json.parse("""{"name":"waldo","colour":{"hex":"#8B4513"},"type":"dog"}""").as[Pet]

    Seq(cat1, cat2, dog).foreach(println)

  }

}
