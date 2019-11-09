name := "micro-service-workshop"
organization in ThisBuild := "seveneves"

scalaVersion in ThisBuild := "2.13.1"

val `user-created-proto` = project
val `user-app` = project.dependsOn(`user-created-proto`)
val `message-board-app` = project.dependsOn(`user-created-proto`)

enablePlugins(GitVersioning)

git.useGitDescribe := true

dockerComposeIgnore := true
