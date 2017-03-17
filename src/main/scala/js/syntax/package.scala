package js

import play.api.libs.json.{JsObject, JsValue}

import scala.language.implicitConversions

package object syntax {

  implicit def toJsObject(pair: Tuple2[String, JsValue]): JsObject = JsObject(Seq(pair._1 -> pair._2))

}
