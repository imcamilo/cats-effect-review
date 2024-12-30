val scala3Version = "3.6.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "cats-effects-review",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.2.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
