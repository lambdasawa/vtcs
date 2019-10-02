package io.lambdasawa.vtcs.web

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import io.lambdasawa.vtcs.core.config.Config
import io.lambdasawa.vtcs.core.service.KVS

import scala.concurrent.ExecutionContext

class Routing()(implicit ec: ExecutionContext, mat: Materializer) {

  def route(config: Config): Route = {

    val kvs = new KVS(config)

    pathSingleSlash {
      get {
        getFromResource(s"public/index.html")
      }
    } ~ path("assets" / Remaining) { pathRest =>
      get {
        getFromResource(pathRest)
      }
    } ~ path("stats") {
      complete {
        kvs.fetchStats()
      }
    }

  }

}
