import AssemblyKeys._

name := "tttish"

version := "0.2"

organization := "moonlighters"

scalaVersion := "2.9.2"

mainClass := Some("ru.moonlighters.tttish.Main")

libraryDependencies += "net.liftweb"  % "lift-json_2.9.1"    % "2.4"

libraryDependencies += "org.scalaj"  %% "scalaj-time"  % "0.6"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.9.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.10"

// Next two are needed for dispatch
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.2"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.2"

assemblySettings

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion)

buildInfoPackage := "ru.moonlighters.tttish"
