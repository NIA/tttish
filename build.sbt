import AssemblyKeys._

name := "tttish"

version := "0.1.2013"

organization := "moonlighters"

scalaVersion := "2.9.2"

mainClass := Some("ru.moonlighters.tttish.Main")

libraryDependencies += "net.liftweb"  % "lift-json_2.9.1"    % "2.4"

libraryDependencies += "org.scalaj"  %% "scalaj-time"  % "0.6"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.9.5"

// Next two are needed for dispatch
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.2"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.2"

assemblySettings
