package io.lambdasawa.vtcs.shared.message
import io.lambdasawa.vtcs.shared.message.Stats.StatItem

case class Stats(
    stats: Seq[StatItem] = Seq()
)

object Stats {
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
}
