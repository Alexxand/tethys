lazy val commonSettings = Seq(
  version := "0.28.0",
  organization := "com.tethys-json",
  scalaVersion := "3.2.0",
  crossScalaVersions := Seq("2.13.10", "3.2.0"),
  Compile / unmanagedSourceDirectories ++= {
    def extraDirs(suffix: String) = Seq(file(sourceDirectory.value.getPath + "/main/scala" + suffix))

    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, y)) if y >= 13 =>
        extraDirs("-2.13+")
      case Some((3, _)) =>
        extraDirs("-3")
      case _ => Nil
    }
  },
  licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/tethys-json/tethys")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/tethys-json/tethys"),
      "scm:git@github.com:tethys-json/tethys.git"
    )
  ),
  developers := List(
    Developer(
      id = "eld0727",
      name = "Aleksei Otts",
      email = "eld0727@gmail.com",
      url = url("https://github.com/eld0727")
    ),
    Developer(
      id = "REDNBLACK",
      name = "Boris Potepun",
      email = "boris.p@protonmail.com",
      url = url("https://github.com/REDNBLACK")
    ),
    Developer(
      id = "MrIrre",
      name = "Erlan Zhaygutov",
      email = "zhaigutov.erlan@gmail.com",
      url = url("https://github.com/MrIrre")
    )
  ),
  credentials ++= Option(Path.userHome / ".config" / "sbt" / ".tethys-credentials")
    .filter(_.exists())
    .map(Credentials(_)),
  publishMavenStyle := true,
  publishTo := {
    if (isSnapshot.value)
      Some(Opts.resolver.sonatypeSnapshots)
    else
      sonatypePublishToBundle.value
  },
  Test / publishArtifact := false
)

lazy val testSettings = Seq(
  libraryDependencies ++=  Seq(
    "org.scalatest" %% "scalatest-flatspec"       % "3.2.15" % Test,
    "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.15" % Test
  )
)

lazy val tethys = project.in(file("."))
  .settings(
    publishTo := None,
    commonSettings
  )
  .aggregate(core, `macro-derivation`, `jackson-211`, `jackson-212`, `jackson-213`, json4s, circe, refined)

lazy val modules = file("modules")

lazy val core = project.in(modules / "core")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-core",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, y)) if y >= 13 =>
          Seq(
            "org.scala-lang" % "scala-compiler" % scalaVersion.value % Provided
          )
        case _ => Seq.empty
      }
    }
  )

lazy val `macro-derivation` = project.in(modules / "macro-derivation")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-derivation",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, y)) if y >= 13 =>
          Seq(
            "org.scala-lang" % "scala-compiler" % scalaVersion.value % Provided
          )
        case _ => Seq.empty
      }
    }
//    ,
//    ThisBuild / scalacOptions += "-explain"
//    ,
//    ThisBuild / scalacOptions += "-Xprint:inline"
  )
  .dependsOn(core)

lazy val jacksonSettings = Seq(
  Compile / unmanagedSourceDirectories += modules / "jackson-backend" / "src" / "main",
  Test / unmanagedSourceDirectories    += modules / "jackson-backend" / "src" / "test",
  Test / unmanagedResourceDirectories  += modules / "jackson-backend" / "src" / "test" / "resources"
)

lazy val `jackson-211` = project.in(modules / "jackson-211")
  .settings(commonSettings)
  .settings(jacksonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-jackson211",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.11.4"
    )
  )
  .dependsOn(core)

lazy val `jackson-212` = project.in(modules / "jackson-212")
  .settings(commonSettings)
  .settings(jacksonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-jackson212",
    libraryDependencies ++= Seq(
        "com.fasterxml.jackson.core" % "jackson-core" % "2.12.7"
      )
  )
  .dependsOn(core)

lazy val `jackson-213` = project.in(modules / "jackson-213")
  .settings(commonSettings)
  .settings(jacksonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-jackson213",
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-core" % "2.13.2"
    )
  )
  .dependsOn(core)

lazy val circe = project.in(modules / "circe")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.4"
    )
  )
  .dependsOn(core, `jackson-212` % Test)

lazy val json4s = project.in(modules / "json4s")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-json4s",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-core" % "4.0.6"
    )
  )
  .dependsOn(core)

lazy val enumeratum = project.in(modules / "enumeratum")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(scalaVersion := "2.13.8")
  .settings(crossScalaVersions := Seq("2.13.8"))
  .settings(
    name := "tethys-enumeratum",
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % "1.7.2"
    )
  )
  .dependsOn(core)

lazy val refined = project.in(modules / "refined")
  .settings(commonSettings)
  .settings(testSettings)
  .settings(
    name := "tethys-refined",
    libraryDependencies ++= Seq(
      "eu.timepit" %% "refined" % "0.10.1"
    )
  )
  .dependsOn(core)

lazy val benchmarks = project.in(modules / "benchmarks")
  .settings(commonSettings)
  .settings(
    publishTo := None,
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.6",
      "org.json4s" %% "json4s-native" % "4.0.5",
      "org.json4s" %% "json4s-jackson" % "4.0.5",
      "io.circe" %% "circe-core" % "0.14.2",
      "io.circe" %% "circe-generic" % "0.14.2",
      "io.circe" %% "circe-jawn" % "0.14.2",
      "io.circe" %% "circe-jackson210" % "0.14.0",
      "com.typesafe.play" %% "play-json" % "2.10.0-RC6",
      "org.knowm.xchart" % "xchart" % "3.8.1" exclude("de.erichseifert.vectorgraphics2d", "VectorGraphics2D") withSources()
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => Seq("-Ymacro-annotations")
        case _             => Nil
      }
    }
  )
  .dependsOn(core, `macro-derivation`, `jackson-211`)
  .enablePlugins(JmhPlugin)
