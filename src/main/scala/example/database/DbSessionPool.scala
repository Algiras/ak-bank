package example.database

import cats.effect.{Async, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import example.config.Database

object DbSessionPool {
  val driverClassName = "org.h2.Driver"

  def make[F[_]: Async](
      config: Database
  ): Resource[F, HikariTransactor[F]] =
    for {
      connectEC <- ExecutionContexts.fixedThreadPool[F](config.maxPoolSize.value)
      transactor <- HikariTransactor.newHikariTransactor[F](
        driverClassName = driverClassName,
        url = config.url.value,
        user = config.user.value,
        pass = config.password.value,
        connectEC = connectEC
      )
    } yield transactor
}
