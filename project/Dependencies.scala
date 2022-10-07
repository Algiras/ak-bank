import sbt._

object Dependencies {
  object Version {
    val cats        = "2.8.0"
    val catsEffect  = "3.3.12"
    val circe       = "0.14.2"
    val http4s      = "0.23.15"
    val logback     = "1.4.1"
    val log4Cats    = "2.4.0"
    val tapir       = "1.1.0"
    val derevo      = "0.13.0"
    val sttpApiSpec = "0.2.1"
    val refined     = "0.10.1"
    val newType     = "0.4.4"
    val ciris       = "2.4.0"
    val sttp        = "3.8.2"
    val doobie      = "1.0.0-RC2"
    val h2          = "2.1.214"
    val flyway      = "9.3.0"
    val squants     = "1.8.3"
    val specs2      = "4.17.0"
    val specs2Cats  = "1.4.0"
  }

  val catsCore             = "org.typelevel"                 %% "cats-core"           % Version.cats
  val catsEffect           = "org.typelevel"                 %% "cats-effect"         % Version.catsEffect
  val circeCore            = "io.circe"                      %% "circe-core"          % Version.circe
  val circeGeneric         = "io.circe"                      %% "circe-generic"       % Version.circe
  val circeRefined         = "io.circe"                      %% "circe-refined"       % Version.circe
  val http4sCirce          = "org.http4s"                    %% "http4s-circe"        % Version.http4s
  val http4sDsl            = "org.http4s"                    %% "http4s-dsl"          % Version.http4s
  val http4sEmberServer    = "org.http4s"                    %% "http4s-ember-server" % Version.http4s
  val http4sEmberClient    = "org.http4s"                    %% "http4s-ember-client" % Version.http4s
  val log4Cats             = "org.typelevel"                 %% "log4cats-slf4j"      % Version.log4Cats
  val sttpApiSpecCirceYaml = "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"  % Version.sttpApiSpec
  val tapirCats            = "com.softwaremill.sttp.tapir"   %% "tapir-cats"          % Version.tapir
  val tapirCirce           = "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"    % Version.tapir
  val tapirCore            = "com.softwaremill.sttp.tapir"   %% "tapir-core"          % Version.tapir
  val tapirHttp4s          = "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server" % Version.tapir
  val tapirOpenApiDocs     = "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"  % Version.tapir
  val tapirSwaggerUi       = "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui"    % Version.tapir
  val tapirDerevo          = "com.softwaremill.sttp.tapir"   %% "tapir-derevo"        % Version.tapir
  val tapirRefined         = "com.softwaremill.sttp.tapir"   %% "tapir-refined"       % Version.tapir
  val tapirNewType         = "com.softwaremill.sttp.tapir"   %% "tapir-newtype"       % Version.tapir
  val derevoCirce          = "tf.tofu"                       %% "derevo-circe"        % Version.derevo
  val derevoCats           = "tf.tofu"                       %% "derevo-cats"         % Version.derevo
  val logback              = "ch.qos.logback"                 % "logback-classic"     % Version.logback
  val newType              = "io.estatico"                   %% "newtype"             % Version.newType
  val refinedCore          = "eu.timepit"                    %% "refined"             % Version.refined
  val refinedCats          = "eu.timepit"                    %% "refined-cats"        % Version.refined
  val derevoCatsTagless    = "tf.tofu"                       %% "derevo-cats-tagless" % Version.derevo
  val ciris                = "is.cir"                        %% "ciris"               % Version.ciris
  val cirisRefined         = "is.cir"                        %% "ciris-refined"       % Version.ciris
  val doobieCore           = "org.tpolecat"                  %% "doobie-core"         % Version.doobie
  val doobieRefined        = "org.tpolecat"                  %% "doobie-refined"      % Version.doobie
  val doobieH2             = "org.tpolecat"                  %% "doobie-h2"           % Version.doobie
  val doobieHikari         = "org.tpolecat"                  %% "doobie-hikari"       % Version.doobie
  val h2                   = "com.h2database"                 % "h2"                  % Version.h2
  val flywayCore           = "org.flywaydb"                   % "flyway-core"         % Version.flyway
  val squants              = "org.typelevel"                 %% "squants"             % Version.squants

  // Testing
  val spec2       = "org.specs2"                    %% "specs2-core"                    % Version.specs2     % Test
  val spec2Cats   = "org.typelevel"                 %% "cats-effect-testing-specs2"     % Version.specs2Cats % Test
  val spec2Doobie = "org.tpolecat"                  %% "doobie-specs2"                  % Version.doobie     % Test
  val sttp        = "com.softwaremill.sttp.client3" %% "core"                           % Version.sttp       % Test
  val sttpCats    = "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % Version.sttp       % Test
  val sttpTapir   = "com.softwaremill.sttp.tapir"   %% "tapir-sttp-client"              % Version.tapir      % Test
}
