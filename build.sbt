name := "Dotty Cats"

version := "1.0"

scalaVersion := "2.12.6"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-free" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-kernel" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-laws" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-macros" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-testkit" % "1.4.0"

libraryDependencies += "org.typelevel" %% "cats-effect" % "1.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"
libraryDependencies += "com.typesafe.play" % "play-json_2.12" % "2.6.0-M5"
libraryDependencies += "com.propensive" %% "contextual-examples" % "1.0.0"

libraryDependencies += "io.monix" %% "monix" % "3.0.0-M3"

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-language:postfixOps",
  "-deprecation",
  "-Ypartial-unification"
)
 
