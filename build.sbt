lazy val root = project
  .in(file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "neljas",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    resolvers ++= Resolvers.all,
    libraryDependencies ++= Dependencies.all,
    wartremoverErrors ++= Warts.unsafe
  )
