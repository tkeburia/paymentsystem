assemblyJarName in assembly := "payment_system.jar"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
      name := "PaymentSystem",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.10.1"
    )

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "org.projectlombok" % "lombok" % "1.12.6",
  javaJpa,
  "com.h2database" % "h2" % "1.4.194",
  "org.hibernate" % "hibernate-core" % "5.2.0.Final",
  jdbc,
  "org.yaml" % "snakeyaml" % "1.18",
  "com.google.guava" % "guava" % "23.0",
  "com.google.inject.extensions" % "guice-multibindings" % "4.1.0",
  "com.jayway.restassured" % "rest-assured" % "2.9.0" % "test"
)

assemblyMergeStrategy in assembly := {
  case x: String if x.contains("javax/transaction") => MergeStrategy.first
  case x: String if x.contains("reference-overrides.conf") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

