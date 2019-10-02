package io.lambdasawa.vtcs.core.service

import com.redis.RedisClient
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.lambdasawa.vtcs.core.config.Config
import io.lambdasawa.vtcs.core.service.KVS.{Keys, Stats}

import scala.concurrent.{ExecutionContext, Future}

class KVS(config: Config)(implicit ec: ExecutionContext) {

  val redis = new RedisClient(
    config.redis.host,
    config.redis.port,
    config.redis.db,
    config.redis.password,
  )

  def saveStats(stat: Stats): Future[Unit] = Future {
    redis.set(Keys.Stats, stat.asJson.noSpaces)
  }

  def fetchStats(): Future[Stats] = Future {
    redis
      .get(Keys.Stats)
      .flatMap { v =>
        decode[Stats](v).toOption
      }
      .getOrElse(KVS.Stats())
  }

}

object KVS {

  case class Stats(
      stats: Seq[StatItem] = Seq()
  )

  case class StatItem(
      channelId: String,
      channelTitle: String,
      videoId: String,
      videoTitle: String,
      chatCount: Int,
      superChatAmountTotal: Long,
      duration: Long,
      hourlyPay: Long,
      startAt: Long,
  )

  object Keys {
    val Stats = "stats"
  }

}
