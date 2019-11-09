name := "user-created-proto"

libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value,
)

dockerComposeIgnore := true
