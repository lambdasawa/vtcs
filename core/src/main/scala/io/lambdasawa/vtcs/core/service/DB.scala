package io.lambdasawa.vtcs.core.service

import java.sql.Timestamp

import io.lambdasawa.vtcs.core.table.Tables
import io.lambdasawa.vtcs.core.table.Tables.{ChannelsRow, ChatMessagesRow}
import io.lambdasawa.vtcs.core.service.DB.MessageStat
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class DB(confPath: String = "db")(implicit ec: ExecutionContext) {

  private val db = Database.forConfig(confPath)

  def saveChannels(channels: Seq[Tables.ChannelsRow]): Future[Unit] = {
    val targetChanelIds = channels.map(_.channelId).toSet

    val query = for {
      dbChannels <- Tables.Channels.filter(_.channelId.inSet(targetChanelIds)).result
      dbChannelIds = dbChannels.map(_.channelId).toSet
      _ <- DBIO.seq(
        channels.filter(c => dbChannelIds(c.channelId)).map { c =>
          Tables.Channels
            .filter(_.channelId === c.channelId)
            .map(p => (p.title, p.updatedAt))
            .update((c.title, new Timestamp(System.currentTimeMillis())))
        }: _*
      )
      _ <- DBIO.seq(
        channels.filter(c => !dbChannelIds(c.channelId)).map { c =>
          Tables.Channels += ChannelsRow(
            id = 0,
            channelId = c.channelId,
            title = c.title,
            createdAt = new Timestamp(System.currentTimeMillis()),
            updatedAt = new Timestamp(System.currentTimeMillis()),
          )
        }: _*
      )
    } yield ()

    db.run(query).map(_ => ())
  }

  def saveVideo(video: Tables.VideosRow): Future[Int] =
    db.run {
      Tables.Videos += video
    }

  def saveChat(chatPage: Tables.ChatPagesRow, chatMessages: Seq[ChatMessagesRow]): Future[Unit] = {
    db.run(
      DBIO.seq(
        Tables.ChatPages += chatPage,
        Tables.ChatMessages ++= chatMessages,
      )
    )
  }

  def findChannels(channelIds: Seq[String]): Future[Seq[Tables.ChannelsRow]] =
    db.run(
      Tables.Channels
        .filter(_.channelId.inSet(channelIds))
        .result
    )


  def findVideos(videoIds: Seq[String]): Future[Seq[Tables.VideosRow]] =
    db.run(
      Tables.Videos
        .filter(_.videoId.inSet(videoIds))
        .result
    )

  def findVideosByChatId(chatIds: Seq[String]): Future[Seq[Tables.VideosRow]] =
    db.run(
      Tables.Videos
        .filter(_.chatId.inSet(chatIds))
        .result
    )

  def findMessageStats(): Future[Seq[MessageStat]] =
    db.run(
      Tables.ChatMessages
        .groupBy(_.chatId)
        .map {
          case (chatId, group) =>
            (
              chatId,
              group.map(_.message).length,
              group.map(_.amount).sum
            )
        }
        .result
        .map {
          _.map {
            case (chatId, chatCount, amountTotal) =>
              MessageStat(chatId, chatCount, amountTotal)
          }
        }
    )

}

object DB {

  case class MessageStat(
      chatId: String,
      chatCount: Int,
      amountTotal: Option[Long],
  )

}
