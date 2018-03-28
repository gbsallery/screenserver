package uk.co.vivalogic

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.concurrent.Worker

import akka.actor.ActorSystem
import spray.http.StatusCodes
import spray.routing.SimpleRoutingApp
import Routing._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.image.{Image, WritableImage}
import scalafx.scene.paint.Color._
import scalafx.scene.text.FontSmoothingType
import scalafx.scene.web.WebView
import scalafx.scene.{Scene, SnapshotResult}
import scalafx.stage.Stage

/*
Support different screen sizes/pixel orders
Document
Partner with Imp
*/

object ScreenServer extends JFXApp with SimpleRoutingApp {
  implicit val system = ActorSystem()

  System.setProperty("prism.text", "t2k") // Mmmm, sub-pixel rendering bugs in JavaFX cause severe dropouts with small fonts. So, beat it with a spoon until it goes away.
  System.setProperty("prism.lcdtext", "false")

  Console.println("Hello, cruel world")

  val route = startServer(interface = "0.0.0.0", port = 8077) {
    pathPrefix("render") {
      authenticationRoute
    } ~ complete(StatusCodes.BadRequest, "Hello. Perhaps you'd like to render something? The Vivalogic Screen Server is available under /render")
  }

  def render(url: String, w: Int, h: Int): Image = {
    var renderedImage: Option[WritableImage] = None
    val ctx = JavaFXExecutionContext.javaFxExecutionContext

    val rendering = Future {
      Platform.implicitExit = false

      val browser = new WebView() // If leak persists, look to re-use WebViews
      val webEngine = browser.getEngine
      browser.setCache(false) // Do not cache bitmaps
      browser.setFontSmoothingType(FontSmoothingType.Gray)

      val stage = new Stage {
        outer =>
          title = "Vivalogic Screen Server"
          scene = new Scene {
            fill = Black
            content = browser
          }
        width = w
        height = h
      }

      stage.show()

      webEngine.getLoadWorker.stateProperty.addListener(
        new ChangeListener[Worker.State] {
          def callback(r: SnapshotResult): Unit = {
            var count:Int = 0
            def handler(t: Long): Unit = {
              count = count + 1
              if (count == 8) { // Wait a few ticks for WebEngine to *actually* render
                val wi = new WritableImage(w, h)
                stage.getScene.snapshot(wi)
                renderedImage = Some(wi)
                stage.hide()
              }
            }
            AnimationTimer(handler).start()
          }

          def changed(p1: ObservableValue[_ <: Worker.State], oldVal: Worker.State, newVal: Worker.State) {
            if (newVal == Worker.State.SUCCEEDED) {
              val wi = new WritableImage(w, h)
              stage.getScene.snapshot(callback _, wi)
            }
          }
        }
      )

      webEngine.load(url)
    }(ctx:ExecutionContext)

    // TODO: Error handling, here
    Await.result(rendering, 5 seconds)

    while (renderedImage.isEmpty) {
      Thread.sleep(100) // This is odious, obviously.
      // Crappy excuse: sorting this out would take time, and I am currently blocking the critical path (which culminates in getting the H-bridge driver working on the Imp)
    }

    renderedImage.get
  }
}