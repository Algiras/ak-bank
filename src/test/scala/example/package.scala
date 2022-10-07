import cats.effect.{IO, Resource}
import cats.implicits._
import ciris.Secret
import com.comcast.ip4s.{Host, Port}
import doobie.hikari.HikariTransactor
import eu.timepit.refined.auto._
import example.config.{Config, Database}
import org.http4s
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.model.Uri

package object example {
  val app = config.App(
    name = "Test",
    version = "0.0.0"
  )

  val databaseConfig: Database = config.Database(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    user = "auser",
    password = Secret("sa"),
    maxPoolSize = 1
  )

  val serverConfig: config.Server = (
    Host.fromString("localhost").toRight(new RuntimeException("Invalid host")),
    Port.fromInt(8080).toRight(new RuntimeException("Invalid port"))
  ).mapN(config.Server).toTry.get

  val testConfig: Config = config.Config(app, serverConfig, databaseConfig)

  def fromHttp4sUrl(uri: http4s.Uri): IO[Uri] = IO.fromEither(
    Uri.parse(uri.renderString).left.map(error => new RuntimeException(error))
  )

  val clientResource: Resource[IO, Client[IO]] = for {
    server <- App.runServer[IO](testConfig)
    uri    <- Resource.liftK[IO](fromHttp4sUrl(server.baseUri))
    httpBE <- AsyncHttpClientCatsBackend.resource[IO]()
  } yield Client.make(uri, httpBE)

  val databaseResource: Resource[IO, HikariTransactor[IO]] = App.setupDB[IO](testConfig)
}
