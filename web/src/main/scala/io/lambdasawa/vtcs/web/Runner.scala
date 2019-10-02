package io.lambdasawa.vtcs.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import io.lambdasawa.vtcs.core.config.Config

import scala.io.StdIn
import scala.util.{Failure, Success}

object Runner {

  def run(): Unit = {
    implicit val system: ActorSystem    = ActorSystem("vtcs-web")
    implicit val mat: ActorMaterializer = ActorMaterializer()
    import system.dispatcher

    val config = Config.load()
    system.log.info("config {}", config)

    val routing = new Routing()
    Http().bindAndHandle(
      DebuggingDirectives.logRequestResult("AllRequest")(routing.route(config)),
      config.app.web.host,
      config.app.web.port,
    )

    StdIn.readLine()
    system
      .terminate()
      .onComplete {
        case Success(v) =>
          system.log.info("stopped. {}", v)
        case Failure(ex) =>
          system.log.error(ex, "stopped.")
      }
  }

}
