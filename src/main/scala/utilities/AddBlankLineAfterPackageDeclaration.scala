package utilities

import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

object AddBlankLineAfterPackageDeclaration {

  import better.files._
  import cats.implicits._

  val packageDeclarationLine: String => Boolean =
    """(?i)\s*package(?! object).+""".r.pattern.asPredicate.test

  def processFile(file: File): Either[String, Unit] =
    Either.catchNonFatal {
      println(file.pathAsString)
      val (pckgs, rest) = file.lines.partition(packageDeclarationLine)
      file.printLines(List(pckgs.filter(_.trim.nonEmpty), List(""), rest.dropWhile(_.trim.isEmpty)).flatten)(Seq(TRUNCATE_EXISTING))
      ()
    }.leftMap(_.getMessage)

  def main(args: Array[String]): Unit = {
    val program: Either[String, Unit] =
      for {
        root <- args.headOption.toRight("must provide at least one command line argument")
        files <- Right(File(root).glob("**/*.scala").toList)
        _ <- files.traverse_(processFile)
      } yield ()

    program.fold(System.err.println, println)
  }

}
