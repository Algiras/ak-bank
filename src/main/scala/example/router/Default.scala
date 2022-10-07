package example.router

import example.definition.ErrorResponse
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

object Default {
  val defaultRoute = endpoint.errorOut(jsonBody[ErrorResponse])
}
