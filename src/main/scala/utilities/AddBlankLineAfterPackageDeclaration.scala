package utilities

import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

object AddBlankLineAfterPackageDeclaration {

  import better.files._
  import cats.implicits._

  val packageDeclarationLine: String => Boolean =
    """(?i)\s*package(?! object).+""".r.pattern.asPredicate.test

  val processFile: File => Either[String, Unit] = file =>
    Either
      .catchNonFatal {
        println(file.pathAsString)
        val (pckgs, rest) = file.lines.partition(packageDeclarationLine)
        file.printLines(List(pckgs.filter(_.trim.nonEmpty), List(""), rest.dropWhile(_.trim.isEmpty)).flatten)(
          Seq(TRUNCATE_EXISTING))
        ()
      }
      .leftMap(_.getMessage)

  val program: Array[String] => Either[String, Unit] = args =>
    for {
      root  <- args.headOption.toRight("must provide at least one command line argument")
      files <- Right(File(root).glob("**/*.scala").toList)
      _     <- files.traverse_(processFile)
    } yield ()

  val run: Either[String, Unit] => Unit =
    _.fold(System.err.println, println)

  def main(args: Array[String]): Unit =
    program andThen run

}
