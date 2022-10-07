package example.config

import cats.implicits._
import ciris._
import ciris.refined._
import com.comcast.ip4s.{Host, Port}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.types.string.NonEmptyString

case class App(name: NonEmptyString, version: NonEmptyString)
case class Database(url: NonEmptyString, user: NonEmptyString, password: Secret[String], maxPoolSize: Int Refined Positive)
case class Server(host: Host, port: Port)
case class Config(app: App, server: Server, database: Database)

object Config {
  val dbConfig = (
    env("DB_URL").as[NonEmptyString],
    env("DB_USER").as[NonEmptyString],
    env("DB_PASSWORD").as[String].secret,
    env("DB_POOL_SIZE").as[Int Refined Positive]
  ).parMapN(Database)

  val appConfig = (
    env("APP_NAME").as[NonEmptyString].default("Bank"),
    env("APP_VERSION").as[NonEmptyString]
  ).parMapN(App)

  implicit val serverHostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder[String, String].mapOption("Host")(Host.fromString)
  implicit val serverPortDecoder: ConfigDecoder[String, Port] =
    ConfigDecoder[String, Int].mapOption("Port")(Port.fromInt)

  val serverConfig = (
    env("HOST").as[Host],
    env("PORT").as[Port]
  ).parMapN(Server)

  val config = (appConfig, serverConfig, dbConfig).parMapN((app, server, database) => new Config(app, server, database))
}
