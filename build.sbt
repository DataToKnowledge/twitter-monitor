name := "kafka-producer"

version := "0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Maven central" at "http://repo1.maven.org/maven2/"
)

libraryDependencies ++= {

  val akkaV = "2.3.11"
  val sprayV = "1.3.3"

  val spray = Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.3.1"
  )

  val akka = Seq(
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.akka"   %% "akka-stream-experimental" % "1.0-RC3",
    "com.typesafe.akka"   %%  "akka-slf4j"              % akkaV,
    "com.typesafe.akka"   %%  "akka-contrib"            % akkaV,
    "ch.qos.logback"     % "logback-classic"            % "1.0.13"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scalactic" %% "scalactic" % "2.2.4"
  )

  val twitter = Seq(
    "org.twitter4j" % "twitter4j-core" % "4.0.3",
    "org.twitter4j" % "twitter4j-stream" % "4.0.3",
    "com.twitter" % "hbc-core" % "2.2.0",
    "com.twitter" % "hbc-twitter4j" % "2.2.0"
  )

  val kafka = Seq(
    //"org.apache.kafka" % "kafka-clients" % "0.8.2.1",
    "org.apache.kafka" % "kafka_2.11" % "0.8.2.1"
  )

  spray ++ akka ++ test ++ twitter ++ kafka
}