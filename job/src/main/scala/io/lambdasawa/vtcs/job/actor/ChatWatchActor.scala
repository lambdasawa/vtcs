package io.lambdasawa.vtcs.job.actor
import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, PoisonPill}
import io.lambdasawa.vtcs.core.table.Tables
import io.lambdasawa.vtcs.job.actor.ChatWatchActor.{Polling, PollingWithPageToken}
import io.lambdasawa.vtcs.core.service.{DB, YouTubeService}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ChatWatchActor(
    chatId: String,
    db: DB,
    youTubeService: YouTubeService,
) extends Actor
    with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case Polling =>
      self ! PollingWithPageToken(None)

    case PollingWithPageToken(token) =>
      log.info(s"start chat watching. chat-id=$chatId token=$token")
      saveChats(token)
        .onComplete {
          case Success((page, _)) =>
            log.info(s"success chat watching. chat-id=$chatId token=$token")
            schedulePolling(page)
          case Failure(ex) =>
            log.error(ex, s"failed chat watching. chat-id=$chatId token=$token")
        }

    case msg =>
      log.warning("unknown message. {}", msg)
  }

  def saveChats(nextPageToken: Option[String]): Future[(Tables.ChatPagesRow, Seq[Tables.ChatMessagesRow])] =
    youTubeService
      .getChats(chatId, nextPageToken)
      .flatMap { cp =>
        val page = Tables.ChatPagesRow(
          id = 0,
          chatId = chatId,
          pageToken = cp.pageToken,
          nextPageToken = cp.nextPageToken,
          pollingInterval = cp.pollingIntervalMillis,
          offlineAt = cp.offlineAt.map(new Timestamp(_)),
          createdAt = new Timestamp(System.currentTimeMillis()),
        )
        val chats = cp.chats.map { c =>
          Tables.ChatMessagesRow(
            id = 0,
            messageId = c.id,
            chatId = chatId,
            pageToken = cp.pageToken,
            message = c.message,
            amount = c.amount,
            currency = c.currency,
            publishedAt = new Timestamp(c.publishedAt),
            createdAt = new Timestamp(System.currentTimeMillis()),
          )
        }
        db.saveChat(page, chats).map(_ => (page, chats))
      }

  def schedulePolling(page: Tables.ChatPagesRow): Unit = {
    page.nextPageToken match {
      case Some(npt) =>
        log.info(s"scheduling next chat watching. chat-id=$chatId delay=${page.pollingInterval.milliseconds}")
        context.system.scheduler.scheduleOnce(
          page.pollingInterval.milliseconds,
          self,
          PollingWithPageToken(Option(npt))
        )
      case _ =>
        log.info(s"finish chat watching. chat-id=$chatId")
        self ! PoisonPill
    }
  }

}

object ChatWatchActor {

  case object Polling
  case class PollingWithPageToken(nextPageToken: Option[String])

  def name(chatId: String): String = s"chat-watch-actor-$chatId"

}
