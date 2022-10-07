package example.database

import cats.effect.Sync
import org.flywaydb.core.Flyway
import example.config.Database

object Migration {
  def apply[F[_]: Sync](config: Database): F[Unit] = Sync[F].delay {
    Flyway
      .configure()
      .dataSource(
        config.url.value,
        config.user.value,
        Option(config.password.value).filter(_.nonEmpty).orNull
      )
      .load()
      .migrate()
    ()
  }
}
