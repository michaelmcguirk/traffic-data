name := "Topl"

version := "0.1"

scalaVersion := "2.13.8"


val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.13" % "test",
  "org.typelevel" %% "cats-core" % "2.8.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion

)