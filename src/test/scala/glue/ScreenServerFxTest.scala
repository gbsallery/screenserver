package glue

import javafx.embed.swing.JFXPanel

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import uk.co.vivalogic.ScreenServer

import scalafx.scene.image.Image

// This doesn't run in IntelliJ, due to some Servlet-related oddness
// It also won't run in TeamCity, as it cannot be run in headless mode (check when Java 9 comes out)
class ScreenServerFxTest extends FlatSpec with Matchers with BeforeAndAfterAll {
  new JFXPanel()

  "The screen server" should "render a URL to an Image (twice)" in  {
    val path = new java.io.File( "." ).getCanonicalPath

    val image = new Image("file:" + path + "/src/test/webapp/img/32x202.png")
    println("Loaded image")

    val rendered1 = ScreenServer.render("file:" + path + "/src/test/webapp/barometerTest.html", 32, 202)
    println("First image rendered")
    val rendered2 = ScreenServer.render("file:" + path + "/src/test/webapp/barometerTest.html", 32, 202)
    println("Second image rendered")

    println(image.width.value.toInt + " width")
    assert(imageCompare(rendered1, image))
    assert(imageCompare(rendered2, image))
  }

  def imageCompare(a: Image, b: Image): Boolean = {
    for (x:Int <- 0 to a.width.value.toInt-1) {
      for (y:Int <- 0 to a.height.value.toInt-1) {
        if (a.getPixelReader.getArgb(x, y) != b.getPixelReader.getArgb(x, y)) return false
      }
    }
    true
  }
}