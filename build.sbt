
lazy val scala212 = "2.12.12"
lazy val scala213 = "2.13.3"
lazy val supportedScalaVersions = List(scala212, scala213)

organization := "be.icteam"
name := "stringmapper"

homepage := Some(url("https://github.com/timvw/stringmapper"))
licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
developers := List(Developer("timvw", "Tim Van Wassenhove", "tim@timvw.be", url("https://timvw.be")))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := scala213
crossScalaVersions := supportedScalaVersions
scalaVersion := crossScalaVersions.value.last

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test"
)