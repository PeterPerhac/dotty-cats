name := "Dotty Cats"

version := "1.0"

scalaVersion := "2.13.1"

libraryDependencies += "org.typelevel" %% "cats-core"    % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-free"    % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-kernel"  % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-laws"    % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-macros"  % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-testkit" % "2.1.0"

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.2"

libraryDependencies += "com.typesafe.play" % "play-json_2.13" % "2.8.1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1"

libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.20.3"

libraryDependencies += "org.slf4j" % "slf4j-api"    % "1.7.30"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.30"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0"

libraryDependencies += "io.monix" %% "monix" % "3.1.0"

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-language:postfixOps",
  "-deprecation"
)
