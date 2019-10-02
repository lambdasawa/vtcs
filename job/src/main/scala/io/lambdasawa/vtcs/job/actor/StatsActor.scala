package io.lambdasawa.vtcs.job.actor

import akka.actor.{Actor, ActorLogging}
import io.lambdasawa.vtcs.core.table.Tables.{ChannelsRow, VideosRow}
import io.lambdasawa.vtcs.core.service.DB.MessageStat
import io.lambdasawa.vtcs.core.service.KVS.{StatItem, Stats}
import io.lambdasawa.vtcs.core.service.{DB, KVS}

import scala.util.{Failure, Success}

class StatsActor(db: DB, kvs: KVS) extends Actor with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case _ =>
      log.info("start stats saving.")
      val future =
        for {
          messageStats <- db.findMessageStats()
          videos       <- db.findVideosByChatId(messageStats.map(_.chatId))
          channels     <- db.findChannels(videos.map(_.channelId))
          stats = buildStats(messageStats, videos, channels)
          u <- kvs.saveStats(stats)
        } yield stats
      future.onComplete {
        case Success(stats) =>
          log.info(s"success save stats. $stats")
        case Failure(ex) =>
          log.error(ex, "failed save stats.")
      }
  }

  def buildStats(
      stats: Seq[MessageStat],
      videos: Seq[VideosRow],
      channels: Seq[ChannelsRow],
  ): Stats = {
    val vm = videos.map(v => v.chatId.getOrElse("") -> v).toMap

    val cm = channels.map(c => c.channelId -> c).toMap

    Stats(
      stats
        .map { stat =>
          val v           = vm(stat.chatId)
          val c           = cm(v.channelId)
          val endAt       = v.endTime.map(_.getTime).getOrElse(System.currentTimeMillis()) / 1000
          val startAt     = v.startTime.map(_.getTime).getOrElse(System.currentTimeMillis()) / 1000
          val duration    = endAt - startAt
          val amountTotal = stat.amountTotal.map(_ / 1000 / 1000).getOrElse(0L)
          StatItem(
            channelId = c.channelId,
            channelTitle = c.title,
            videoId = v.videoId,
            videoTitle = v.title,
            chatCount = stat.chatCount,
            superChatAmountTotal = amountTotal,
            duration = duration,
            hourlyPay = (amountTotal.toDouble / (duration.toDouble / 60 / 60)).toLong,
            startAt = v.startTime.map(_.getTime).getOrElse(0L),
          )
        }
        .sortBy(-_.startAt)
    )
  }

}
