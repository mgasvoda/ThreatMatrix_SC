name := "ThreatMatrix"

version := "0.1"

val sparkVersion = "2.3.2"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.scala-lang" % "scala-swing" % "2.11.0-M7"
)

