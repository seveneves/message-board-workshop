addSbtPlugin("org.scalameta"       % "sbt-scalafmt"        % "2.2.1")
addSbtPlugin("com.typesafe.sbt"    % "sbt-git"             % "1.0.0")
addSbtPlugin("com.typesafe.sbt"    % "sbt-native-packager" % "1.4.1")
addSbtPlugin("com.thesamet"        % "sbt-protoc"          % "0.99.25")
addSbtPlugin("com.github.ehsanyou" % "sbt-docker-compose"  % "1.0.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.9.4"
