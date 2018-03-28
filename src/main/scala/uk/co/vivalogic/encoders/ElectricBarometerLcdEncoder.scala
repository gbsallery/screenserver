package uk.co.vivalogic.encoders

import scala.collection.mutable
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
 * Created by gavin on 26/05/15.
 */
object ElectricBarometerLcdEncoder extends Encoder {
  def convertImage(image: Image, w: Int, h: Int): String = {
//    import java.io.File
//    import java.util.Date
//    import javafx.embed.swing.SwingFXUtils
//    import javax.imageio.ImageIO
//    val output = new File("snapshot" + new Date().getTime.toString + ".png")
//    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output)

    // The magic numbers below are derived from a knowledge of the LCD screen size, the image size, the width of a
    // nibble, and the word-by-word addressing scheme of the LCD. Adjust at your peril.

    val encodedString = new mutable.StringBuilder(w * h)
    for (x:Int <- 0 to image.width.value.toInt-1) {
      for (y:Int <- (image.height.value/4).ceil.toInt*4-3 to -3 by -4) {
        encodedString.append(encodeImageNibble(image, x, y))
      }
    }

    encodedString.toString()
  }

  def encodeImageNibble(image: Image, x: Int, y:Int):Char = {
    def safeRead(x: Int, y: Int): Color = {
      if (y < image.height.value.toInt && y >= 0)
        image.pixelReader.get.getColor(x, y)
      else
        Color.Black
    }
    val p1:Int = (1-safeRead(x, y).grayscale.red).round.toInt
    val p2:Int = (1-safeRead(x, y-1).grayscale.red).round.toInt
    val p3:Int = (1-safeRead(x, y-2).grayscale.red).round.toInt
    val p4:Int = (1-safeRead(x, y-3).grayscale.red).round.toInt
    (p1*8 + p2*4 + p3*2 + p4).toHexString.charAt(0)
  }

  override val name: String = "Electric Barometer LCD Encoder"
}
