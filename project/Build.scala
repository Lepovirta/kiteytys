import sbt._

object Build {
}

object Resolvers {
  val http4s = "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

  val all = List(http4s)
}

object Dependencies {
  object http4s {
    val version = "0.12.3"
    val dsl = "org.http4s" %% "http4s-dsl" % version
    val twirl = "org.http4s" %% "http4s-twirl" % version
    val blazeServer = "org.http4s" %% "http4s-blaze-server" % version

    val all = List(dsl, twirl, blazeServer)
  }

  object log {
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
    val logback = "ch.qos.logback" % "logback-classic" % "1.1.6"

    val all = List(scalaLogging, logback)
  }

  object pdf {
    val sPDF = "io.github.cloudify" %% "spdf" % "1.3.3"

    val all = List(sPDF)
  }

  object conf {
    val typesafeConfig = "com.typesafe" % "config" % "1.3.0"

    val all = List(typesafeConfig)
  }

  val all = List.concat(http4s.all, log.all, pdf.all, conf.all)
}

object Warts {
  import wartremover.WartRemover.autoImport.Wart._

  val all = List(
    Any,
    Any2StringAdd,
    AsInstanceOf,
    Enumeration,
    FinalCaseClass,
    IsInstanceOf,
    ListOps,
    MutableDataStructures,
    Nothing,
    Null,
    Option2Iterable,
    OptionPartial,
    Product,
    Return,
    Serializable,
    ToString,
    TryPartial,
    Var
  )
}