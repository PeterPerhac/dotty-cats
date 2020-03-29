package validated

import cats.data.{NonEmptyList, ValidatedNel}
import cats.instances.ListInstances
import cats.syntax.{ApplySyntax, ValidatedSyntax}

import scala.util.matching.Regex

object ContentTypeHeaderValidation extends ValidatedSyntax with ApplySyntax with ListInstances {

  case class AcceptHeader(acceptType: String, version: String, contentType: String)

  val apiSupportedVersions: List[String] = List("1.0", "2.0")
  val HeaderPattern: Regex = """^(application/vnd\.hmrc)\.(.*)\+(.*)$""".r

  def validateHeader(h: AcceptHeader): ValidatedNel[String, AcceptHeader] = {
    val validVersion = h.version.validNel.ensure(NonEmptyList.of("Unsupported version"))(apiSupportedVersions.contains)
    val validContentType =
      h.contentType.validNel.ensure(NonEmptyList.of("Unsupported content type"))("json".equalsIgnoreCase)
    (validVersion, validContentType).mapN { case (_, _) => h }
  }

  def parseHeader(h: String): Option[AcceptHeader] =
    Some(h).collect { case HeaderPattern(at, v, ct) => AcceptHeader(at, v, ct) }

  def testIt(headers: Map[String, String]): Either[NonEmptyList[String], AcceptHeader] =
    for {
      h           <- headers.get("Accept").toRight(NonEmptyList.of("Accept header is missing"))
      validFormat <- parseHeader(h).toRight(NonEmptyList.of("Accept header format is invalid"))
      validHeader <- validateHeader(validFormat).toEither
    } yield validHeader

  def main(args: Array[String]): Unit = {
    //valid ones:
    println(testIt(Map("Accept" -> "application/vnd.hmrc.1.0+json")))
    println(testIt(Map("Accept" -> "application/vnd.hmrc.2.0+json")))

    //invalid ones
    println(testIt(Map("NotAccept" -> "something")))
    println(testIt(Map("Accept"    -> "not valid")))
    println(testIt(Map("Accept"    -> "application/vnd.hmrc.1.1+json")))
    println(testIt(Map("Accept"    -> "application/vnd.hmrc.1.0+xml")))
    println(testIt(Map("Accept"    -> "application/vnd.hmrc.1.1+xml")))
  }

}
