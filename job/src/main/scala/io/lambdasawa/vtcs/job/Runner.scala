package io.lambdasawa.vtcs.job

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import io.lambdasawa.vtcs.job.actor.{ChannelWatchActor, StatsActor}
import io.lambdasawa.vtcs.core.config.Config
import io.lambdasawa.vtcs.core.service.{DB, KVS, YouTubeService}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

object Runner {

  def run(): Unit = {
    val system = ActorSystem("vtcs-job")

    import system.dispatcher

    val config = Config.load()
    system.log.info("config {}", config)

    setupScheduler(system, config)

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

  def setupScheduler(system: ActorSystem, config: Config): Unit = {
    import scala.concurrent.duration._

    implicit val sys: ActorSystem             = system
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val mat: ActorMaterializer       = ActorMaterializer()

    val db             = new DB()
    val kvs            = new KVS(config)
    val youTubeService = new YouTubeService(config)

    val channelListActor = system.actorOf(
      Props(classOf[ChannelWatchActor], config.app.youtube.channels, db, youTubeService),
      "channel-watch"
    )

    system.scheduler.schedule(
      0.second,
      1.minute,
      channelListActor,
      ChannelWatchActor.PollingChat,
    )

    system.scheduler.schedule(
      0.second,
      1.hour,
      channelListActor,
      ChannelWatchActor.PollingDetail,
    )

    val statsActor = system.actorOf(Props(classOf[StatsActor], db, kvs), "stats")

    system.scheduler.schedule(
      0.second,
      20.second,
      statsActor,
      (),
    )

  }

}
