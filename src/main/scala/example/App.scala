package example

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.toSemigroupKOps
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.{HttpRoutes, server}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import example.config._
import example.database.{DbSessionPool, Migration}
import example.router.{Accounts, Operations}
import example.service.H2Bank
import sttp.apispec.openapi.OpenAPI
import sttp.apispec.openapi.circe.yaml._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

object App extends IOApp {
  private def docs(app: App): OpenAPI = OpenAPIDocsInterpreter().toOpenAPI(
    List(
      Accounts.createAccount,
      Accounts.accountBalance,
      Operations.transferMoney
    ),
    app.name.value,
    app.version.value
  )

  def setupBank[F[_]: Async](config: Config) = for {
    _          <- Resource.liftK(Migration[F](config.database))
    transactor <- DbSessionPool.make[F](config.database)
  } yield H2Bank.make(transactor)

  def runServer[F[_]: Async](config: Config, openAPI: Option[OpenAPI] = None): Resource[F, server.Server] =
    for {
      bank <- setupBank(config)
      interpreter = Http4sServerInterpreter[F]()
      accounts    = new Accounts[F](bank)
      operations  = new Operations[F](bank)
      httpApp = (
        interpreter.toRoutes(accounts.createAccountRoute) <+>
          interpreter.toRoutes(accounts.getAccountBalance) <+>
          interpreter.toRoutes(operations.transferMoneyRoute) <+>
          (openAPI match {
            case Some(api) => interpreter.toRoutes(SwaggerUI[F](api.toYaml))
            case None      => HttpRoutes.empty[F]
          })
      ).orNotFound
      server <- EmberServerBuilder
        .default[F]
        .withHost(config.server.host)
        .withPort(config.server.port)
        .withHttpApp(httpApp)
        .build
    } yield server

  override def run(args: List[String]): IO[ExitCode] = for {
    log                        <- Slf4jLogger.create[IO]
    config @ Config(app, _, _) <- Config.config.load[IO]
    _ <- runServer[IO](config, Some(docs(app)))
      .use(appServer => log.info(s"${app.name} v${app.version} started on ${appServer.address.getHostString}") *> IO.never[Unit])
  } yield ExitCode.Success
}
