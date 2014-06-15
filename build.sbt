import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := """rng"""

version := "0.1.0"

scalaVersion := "2.10.2"

packageArchetype.java_application

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"            % "2.2.3",
  "com.typesafe.akka"      %% "akka-slf4j"            % "2.2.3",
  "io.spray"                % "spray-can"             % "1.2.0",
  "io.spray"                % "spray-httpx"           % "1.3.1",
  "io.spray"                % "spray-client"          % "1.3.1",
  "io.spray"                % "spray-routing"         % "1.3.1",
  "io.spray"               %% "spray-json"            % "1.2.6",
  "org.json4s"            %% "json4s-native"          % "3.2.9",
  "org.apache.commons"      % "commons-math3"         % "3.3",
  "org.specs2"             %% "specs2"                % "2.2.2"        % "test",
  "io.spray"                % "spray-testkit"         % "1.2.0"        % "test",
  "com.typesafe.akka"      %% "akka-testkit"          % "2.2.3"        % "test"
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
