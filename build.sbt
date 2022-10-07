lazy val root = (project in file("."))
  .settings(
    Seq(
      scalaVersion := "2.13.6",
      organization := "example",
      scalacOptions ++= Seq(
        "-feature",
        "-Ymacro-annotations",
        "-Xfatal-warnings",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates"
      ),
      Test / console / scalacOptions --= Seq(
        "-Ymacro-annotations",
        "-Xfatal-warnings",
        "-Ywarn-unused:implicits",
        "-Ywarn-unused:imports",
        "-Ywarn-unused:locals",
        "-Ywarn-unused:params",
        "-Ywarn-unused:patvars",
        "-Ywarn-unused:privates"
      ),
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2")) {
          Seq(
            compilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1"),
            compilerPlugin("org.typelevel" % "kind-projector"     % "0.13.2" cross CrossVersion.full)
          )
        } else {
          Seq()
        }
      ),
      testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
      scalafmtOnCompile := true,
      libraryDependencies ++= Seq(
        Dependencies.catsCore,
        Dependencies.newType,
        Dependencies.derevoCats,
        Dependencies.derevoCirce,
        Dependencies.refinedCore,
        Dependencies.refinedCats,
        Dependencies.circeGeneric,
        Dependencies.circeRefined,
        Dependencies.derevoCatsTagless,
        Dependencies.catsCore,
        Dependencies.ciris,
        Dependencies.cirisRefined,
        Dependencies.catsEffect,
        Dependencies.circeCore,
        Dependencies.circeGeneric,
        Dependencies.circeRefined,
        Dependencies.tapirCats,
        Dependencies.tapirCirce,
        Dependencies.tapirCore,
        Dependencies.tapirDerevo,
        Dependencies.tapirRefined,
        Dependencies.tapirNewType,
        Dependencies.derevoCirce,
        Dependencies.circeCore,
        Dependencies.circeGeneric,
        Dependencies.circeRefined,
        Dependencies.tapirCats,
        Dependencies.tapirCirce,
        Dependencies.derevoCirce,
        Dependencies.log4Cats,
        Dependencies.logback,
        Dependencies.http4sCirce,
        Dependencies.http4sDsl,
        Dependencies.http4sEmberServer,
        Dependencies.sttpApiSpecCirceYaml,
        Dependencies.tapirHttp4s,
        Dependencies.tapirOpenApiDocs,
        Dependencies.tapirSwaggerUi,
        Dependencies.tapirCore,
        Dependencies.tapirOpenApiDocs,
        Dependencies.doobieCore,
        Dependencies.doobieHikari,
        Dependencies.doobieH2,
        Dependencies.h2,
        Dependencies.flywayCore,
        Dependencies.doobieRefined,
        Dependencies.squants,
        // Testing
        Dependencies.spec2,
        Dependencies.spec2Cats,
        Dependencies.sttp,
        Dependencies.sttpCats,
        Dependencies.sttpTapir,
        Dependencies.spec2Doobie
      )
    )
  )
