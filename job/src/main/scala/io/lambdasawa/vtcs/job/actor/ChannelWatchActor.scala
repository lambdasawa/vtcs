package io.lambdasawa.vtcs.job.actor

import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, Props}
import io.lambdasawa.vtcs.core.table.Tables
import io.lambdasawa.vtcs.job.actor.ChannelWatchActor.{PollingChat, PollingDetail}
import io.lambdasawa.vtcs.core.service.YouTubeService.Channel
import io.lambdasawa.vtcs.core.service.{DB, YouTubeService}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ChannelWatchActor(
    channels: Seq[String],
    db: DB,
    youTubeService: YouTubeService,
) extends Actor
    with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case PollingChat =>
      log.info(s"channel list watch start. $channels}")
      findLatestVideos
        .flatMap { videos =>
          Future
            .sequence(
              videos.grouped(50).map { vs =>
                youTubeService.getVideos(vs.map(_.videoId))
              }
            )
            .map { vs =>
              vs.flatten
            }
        }
        .flatMap { videos =>
          Future.sequence(
            videos.map { v =>
              saveVideo(v)
            }
          )
        }
        .map { videos =>
          videos.foreach { v =>
            spawnChatWatchActor(v)
          }
        }
        .onComplete {
          case Success(_) =>
            log.info(s"success channel watching. channel-ids=$channels")
          case Failure(ex) =>
            log.error(ex, s"failed channel watching. channel-ids=$channels")
        }

    case PollingDetail =>
      log.info(s"start channel detail watching. $channels")
      findChannels.flatMap(saveChannels).onComplete {
        case Success(_) =>
          log.info(s"success channel detail watching. $channels}")
        case Failure(ex) =>
          log.error(ex, s"failed channel detail watching. $channels}")
      }

    case msg =>
      log.warning(s"unknown message. {}", msg)
  }

  def findLatestVideos: Future[Seq[YouTubeService.Video]] = {
    Future.sequence(
      channels.map { channelId =>
        youTubeService.getLatestVideo(channelId)
      }
    )
  }

  def saveVideo(v: YouTubeService.Video): Future[Tables.VideosRow] = {
    val video = Tables.VideosRow(
      id = 0,
      videoId = v.videoId,
      channelId = v.channelId,
      title = v.title,
      publishedAt = new Timestamp(v.publishedAt),
      chatId = v.activeLiveChatId,
      startTime = v.startTime.map(new Timestamp(_)),
      endTime = v.endTime.map(new Timestamp(_)),
      createdAt = new Timestamp(System.currentTimeMillis()),
    )
    db.saveVideo(video).map(_ => video)
  }

  def spawnChatWatchActor(video: Tables.VideosRow): Unit = {
    for {
      chatId    <- video.chatId
      startTime <- video.startTime
      now            = System.currentTimeMillis()
      started        = startTime.getTime <= now
      ended          = video.endTime.exists(_.getTime <= now)
      name           = ChatWatchActor.name(chatId)
      notExistsChild = context.child(name).isEmpty
      if started && !ended && notExistsChild
    } {
      log.info(s"spawn chat watch actor. chat-id=$chatId")
      val actor = context.actorOf(Props(classOf[ChatWatchActor], chatId, db, youTubeService), name)
      actor ! ChatWatchActor.Polling
    }
  }

  def findChannels: Future[Seq[YouTubeService.Channel]] = {
    Future
      .sequence(
        channels
          .grouped(50)
          .map { ids =>
            youTubeService
              .getChannels(ids)
          }
      )
      .map { channels =>
        channels.toSeq.flatten
      }
  }

  def saveChannels(channels: Seq[Channel]): Future[Unit] =
    db.saveChannels(channels.map { c =>
      Tables.ChannelsRow(
        id = 0,
        channelId = c.channelId,
        title = c.title,
        createdAt = new Timestamp(0),
        updatedAt = new Timestamp(0),
      )
    })

}

object ChannelWatchActor {

  case object PollingChat
  case object PollingDetail

}
