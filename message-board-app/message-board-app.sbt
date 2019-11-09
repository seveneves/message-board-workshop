val akkaHttpVersion = "10.1.10"
val akkaVersion = "2.6.0"
val circeVersion = "0.12.3"

name := "message-board-app"

configs(IntegrationTest)

Defaults.itSettings

libraryDependencies += "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream"       % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor"        % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed"  % akkaVersion
libraryDependencies += "io.circe"          %% "circe-core"        % circeVersion
libraryDependencies += "io.circe"          %% "circe-generic"     % circeVersion
libraryDependencies += "ch.qos.logback"    % "logback-classic"    % "1.2.3"
libraryDependencies += "de.heikoseeberger" %% "akka-http-circe"   % "1.29.1"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0"

libraryDependencies += "org.scalatest" %% "scalatest"   % "3.0.8" % IntegrationTest
libraryDependencies += "org.scalaj"    %% "scalaj-http" % "2.4.2" % IntegrationTest

enablePlugins(DockerPlugin)
enablePlugins(UniversalPlugin)
enablePlugins(JavaAppPackaging)

dockerExposedPorts ++= Seq(8080)
dockerUpdateLatest := true

enablePlugins(DockerCompose)
dockerComposeTestLogging := false
dockerComposeTags += ("message-board-app", version.value)
dockerComposeIgnore := false
