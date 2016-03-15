lazy val root = project
  .in(file("."))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "kiteytys",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    resolvers ++= CustomResolvers.all,
    assemblyJarName in assembly := "kiteytys.jar",
    libraryDependencies ++= Dependencies.all
  )
