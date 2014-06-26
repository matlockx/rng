import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import com.typesafe.sbt.packager.archetypes.ServerLoader.{Upstart, SystemV}

name := """rng"""

version := "0.1.0"

scalaVersion := "2.10.2"

packageArchetype.java_server

serverLoading := Upstart

maintainer in Linux := "Random Number Generator <rng@test.de>"

packageSummary in Linux := "A small package summary"

packageDescription := "A longer description of your application"

debianPackageDependencies in Debian ++= Seq("openjdk-7-jre")

daemonUser in Linux := normalizedName.value

daemonGroup in Linux := normalizedName.value

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"            % "2.3.3",
  "com.typesafe.akka"      %% "akka-slf4j"            % "2.3.3",
  "io.spray"                % "spray-can"             % "1.3.1",
  "io.spray"                % "spray-httpx"           % "1.3.1",
  "io.spray"                % "spray-routing"         % "1.3.1",
  "io.spray"               %% "spray-json"            % "1.2.6",
  "joda-time"             % "joda-time"               % "2.3",
  "org.joda"              % "joda-convert"            % "1.6",
  "ch.qos.logback"        % "logback-classic"         % "1.0.13",
  "org.apache.commons"      % "commons-math3"         % "3.3",
  "org.specs2"             %% "specs2"                % "2.3.12"        % "test",
  "org.specs2"            %% "specs2-matcher-extra" % "2.3.12" % "test",
  "io.spray"                % "spray-testkit"         % "1.3.1"        % "test",
  "com.typesafe.akka"      %% "akka-testkit"          % "2.3.3"        % "test",
  "junit" % "junit" % "4.11" % "test"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

parallelExecution in Test := false

javaOptions ++= Seq("-Xmx512m")

fork in run := true

connectInput in run := true

outputStrategy in run := Some(StdoutOutput)
