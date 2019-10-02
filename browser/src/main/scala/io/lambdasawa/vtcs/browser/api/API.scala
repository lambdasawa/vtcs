package io.lambdasawa.vtcs.browser.api
import io.lambdasawa.vtcs.shared.message.Stats
import org.scalajs.dom.ext.Ajax

import scala.concurrent.{ExecutionContext, Future}

class API()(implicit ec: ExecutionContext) {
  import io.circe._
  import io.circe.generic.auto._
  import io.circe.parser._

  private def getJSON[A: Decoder](url: String): Future[A] =
    for {
      xhr <- Ajax.get(url)
      res <- Future.fromTry(decode[A](xhr.responseText).toTry)
    } yield res

  def getStats: Future[Stats] = getJSON[Stats]("/stats")
}

