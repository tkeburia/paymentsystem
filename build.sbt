name := """PaymentSystem"""
organization := "tkeburia"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "org.projectlombok" % "lombok" % "1.12.6",
  javaJpa,
  "com.h2database" % "h2" % "1.4.194",
  "org.hibernate" % "hibernate-core" % "5.2.0.Final",
  jdbc
)