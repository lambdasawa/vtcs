package io.lambdasawa.vtcs.core.service

import java.io.{File, StringReader}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.youtube.{YouTube, YouTubeScopes}
import com.google.common.collect.Lists
import com.rometools.rome.io.SyndFeedInput
import io.lambdasawa.vtcs.core.config.Config
import io.lambdasawa.vtcs.core.service.YouTubeService.{Channel, Chat, ChatPage, Video}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class YouTubeService(config: Config)(implicit system: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer) {

  val client: YouTube = {
    val httpTransport  = new NetHttpTransport()
    val jacksonFactory = new JacksonFactory()
    val dataDir       = new File(System.getProperty("user.home"), ".credentials/vtcs")
    new YouTube.Builder(
      httpTransport,
      jacksonFactory,
      new AuthorizationCodeInstalledApp(
        new GoogleAuthorizationCodeFlow.Builder(
          httpTransport,
          jacksonFactory,
          config.app.youtube.clientId,
          config.app.youtube.clientSecret,
          Lists.newArrayList(YouTubeScopes.YOUTUBE_READONLY)
        ).setDataStoreFactory(new FileDataStoreFactory(dataDir))
          .setAccessType("offline")
          .build(),
        new LocalServerReceiver.Builder().setHost("localhost").setPort(9000).setCallbackPath("/Callback").build(),
      ).authorize("user")
    ).setApplicationName("ytcs").build
  }

  def getChannels(channelIds: Seq[String]) = Future {
    client
      .channels()
      .list("id,snippet")
      .setId(channelIds.mkString(","))
      .setMaxResults(50L)
      .execute()
      .getItems
      .asScala
      .map { c =>
        Channel(
          c.getId,
          c.getSnippet.getTitle
        )
      }
  }

  def getLatestVideo(
      channelId: String,
  ): Future[Video] = {
    val uri = s"https://www.youtube.com/feeds/videos.xml?channel_id=$channelId"
    Http()
      .singleRequest(
        HttpRequest(
          uri = uri
        )
      )
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          entity.dataBytes.runFold(ByteString(""))(_ ++ _)
        case res =>
          sys.error(s"unknown response. uri=$uri res=$res")
      }
      .map { body =>
        val feed  = new SyndFeedInput().build(new StringReader(body.utf8String))
        val entry = feed.getEntries.asScala.head
        val link  = entry.getLink
        val videoId =
          Query(Uri(link).rawQueryString.getOrElse(""))
            .getOrElse("v", sys.error(s"failed get video id. link=$link"))
        Video(
          videoId = videoId,
          channelId = channelId,
          title = entry.getTitle,
        )
      }
  }

  def getVideos(videoIds: Seq[String])(implicit ec: ExecutionContext): Future[Seq[Video]] = Future {
    client
      .videos()
      .list("id,snippet,liveStreamingDetails")
      .setId(videoIds.mkString(","))
      .setMaxResults(50L)
      .execute()
      .getItems
      .asScala
      .map { video =>
        val live = Option(video.getLiveStreamingDetails)
        Video(
          video.getId,
          video.getSnippet.getChannelId,
          video.getSnippet.getTitle,
          video.getSnippet.getPublishedAt.getValue,
          for {
            l <- live
            i <- Option(l.getActiveLiveChatId)
          } yield i,
          for {
            l <- live
            t <- Option(l.getActualStartTime)
          } yield t.getValue,
          for {
            l <- live
            t <- Option(l.getActualEndTime)
          } yield t.getValue,
        )
      }
  }

  def getChats(chatId: String, pageToken: Option[String])(implicit ec: ExecutionContext) = Future {
    val res = client
      .liveChatMessages()
      .list(chatId, "id, snippet")
      .setMaxResults(2000L)
      .setPageToken(pageToken.getOrElse(""))
      .execute()
    ChatPage(
      pageToken,
      Option(res.getNextPageToken),
      res.getPollingIntervalMillis,
      Option(res.getOfflineAt).map(_.getValue),
      res.getItems.asScala.map { c =>
        val detail = Option(c.getSnippet.getSuperChatDetails)
        Chat(
          c.getId,
          c.getSnippet.getPublishedAt.getValue,
          c.getSnippet.getDisplayMessage,
          for {
            d <- detail
            a <- Option(d.getAmountMicros.longValue())
          } yield a,
          for {
            d <- detail
            c <- Option(d.getCurrency)
          } yield c,
        )
      }
    )
  }

}

object YouTubeService {

  case class Channel(
      channelId: String,
      title: String,
  )

  case class Video(
      videoId: String = "",
      channelId: String = "",
      title: String = "",
      publishedAt: Long = 0L,
      activeLiveChatId: Option[String] = None,
      startTime: Option[Long] = None,
      endTime: Option[Long] = None,
  )

  case class ChatPage(
      pageToken: Option[String],
      nextPageToken: Option[String],
      pollingIntervalMillis: Long,
      offlineAt: Option[Long],
      chats: Seq[Chat]
  )

  case class Chat(
      id: String,
      publishedAt: Long,
      message: String,
      amount: Option[Long],
      currency: Option[String],
  )

}
