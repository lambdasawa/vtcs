package io.lambdasawa.vtcs.browser

import io.lambdasawa.vtcs.browser.pages.IndexPage

object Main {
  def main(args: Array[String]): Unit = {
    import org.scalajs.dom

    dom.window.console.log("hi")

    val component = IndexPage.component(IndexPage.Props())
    val element   = dom.document.getElementById("root")
    component.renderIntoDOM(element)
  }
}
