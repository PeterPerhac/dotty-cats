name := "Dotty Cats"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0-MF"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"
libraryDependencies += "com.typesafe.play" % "play-json_2.12" % "2.6.0-M5"
libraryDependencies += "com.propensive" %% "contextual-examples" % "1.0.0"

libraryDependencies ++= Seq(
  "io.monix" %% "monix-eval" % "2.3.0",
  "io.monix" %% "monix-cats" % "2.3.0"
)