name := "twitter-monitor"

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
    "io.spray"            %%  "spray-json"    % "1.3.2"
  )

  val akka = Seq(
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.akka"   %% "akka-stream-experimental" % "1.0-RC3",
    "com.typesafe.akka"   %%  "akka-slf4j"              % akkaV,
    "com.typesafe.akka"   %%  "akka-contrib"            % akkaV,
    "ch.qos.logback"     % "logback-classic"            % "1.1.3"
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.scalactic" %% "scalactic" % "2.2.5",
    "org.scalacheck" %% "scalacheck" % "1.12.3" % "test"
  )

  val twitter = Seq(
    "org.twitter4j" % "twitter4j-core" % "4.0.3",
    "org.twitter4j" % "twitter4j-stream" % "4.0.3",
    "com.twitter" % "hbc-core" % "2.2.0",
    "com.twitter" % "hbc-twitter4j" % "2.2.0"
  )

  val kafka = Seq(
    "org.apache.kafka" % "kafka-clients" % "0.8.2.1"
  )

  spray ++ akka ++ test ++ twitter ++ kafka
}

javaOptions += "-Xms512m -Xmx2G"
Revolver.settings

enablePlugins(JavaAppPackaging)
bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

enablePlugins(DockerPlugin)
// change the name of the project adding the prefix of the user
packageName in Docker := "dtk/" +  packageName.value
maintainer in Docker := "info@datatotknowledge.it"
//the base docker images
dockerBaseImage := "java:8-jre"
//the exposed port
dockerExposedPorts := Seq(5000)
//exposed volumes
dockerExposedVolumes := Seq("/opt/docker/logs")
