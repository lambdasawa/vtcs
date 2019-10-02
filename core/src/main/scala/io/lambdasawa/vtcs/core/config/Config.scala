package io.lambdasawa.vtcs.core.config

import com.typesafe.config.ConfigFactory
import io.lambdasawa.vtcs.core.config.Config.{AppConfig, RedisConfig}
import pureconfig.generic.auto._

case class Config(
    redis: RedisConfig = RedisConfig(),
    app: AppConfig = AppConfig(),
)

object Config {

  case class RedisConfig(
      host: String = "",
      port: Int = 0,
      db: Int = 0,
      password: Option[String] = None,
  )

  case class AppConfig(
      youtube: YouTubeConfig = YouTubeConfig(),
      web: WebConfig = WebConfig(),
  )

  case class YouTubeConfig(
      clientId: String = "",
      clientSecret: String = "",
      channels: Seq[String] = Seq(),
  )

  case class WebConfig(
      host: String = "",
      port: Int = 0,
  )

  def load(): Config = {
    pureconfig
      .loadConfig[Config](ConfigFactory.load())
      .getOrElse(sys.error("invalid config"))
  }

}
