package io.lambdasawa.vtcs.browser.pages
import io.lambdasawa.vtcs.browser.api.API
import io.lambdasawa.vtcs.shared.message.Stats
import org.scalajs.dom.html.Div

import scala.scalajs.js
import scala.scalajs.js.Date

object IndexPage {
  import japgolly.scalajs.react._
  import japgolly.scalajs.react.vdom.html_<^._

  import scala.concurrent.ExecutionContext.Implicits._

  case class Props()

  case class State(
                    stats: Stats = Stats(),
                  )

  val component =
    ScalaComponent
      .builder[Props]("IndexPage")
      .initialState(State())
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  class Backend($ : BackendScope[Props, State]) {
    val api: API = new API()

    def render(props: Props, state: State): VdomTagOf[Div] =
      <.div(
        <.table(
          ^.className := "table table-sm table-hover table-striped table-bordered",
          <.thead(
            <.tr(
              <.th("配信タイトル"),
              <.th("チャンネル名"),
              <.th("開始日時"),
              <.th("配信時間"),
              <.th("総チャット数"),
              <.th("総スパチャ額"),
              <.th("時給"),
            )
          ),
          <.tbody(
            state.stats.stats.toTagMod { stat =>
              <.tr(
                <.td(
                  ^.style := js.Dictionary(
                    "word-break" -> "break-all",
                    "max-width" -> 600.px,
                  ),
                  <.a(
                    ^.href := s"https://www.youtube.com/watch?v=${stat.videoId}",
                    stat.videoTitle,
                  ),
                ),
                <.td(
                  <.a(
                    ^.href := s"https://www.youtube.com/channel/${stat.channelId}",
                    stat.channelTitle,
                  ),
                ),
                <.td(new Date(stat.startAt).toLocaleString()),
                <.td("%02d:%02d:%02d".format(stat.duration / 3600, stat.duration % 3600 / 60, stat.duration % 60)),
                <.td(stat.chatCount),
                <.td(stat.superChatAmountTotal),
                <.td(stat.hourlyPay),
              )
            }
          ),
        )
      )

    def init: Callback =
      Callback.future {
        api.getStats.map { stats =>
          $.modState(_.copy(stats = stats))
        }
      }
  }
}
