package uk.co.vivalogic.encoders

import scala.collection.mutable
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
 * Created by gavin on 26/05/15.
 */
object MPicoSysEPDEncoder extends Encoder {
  def convertImage(image: Image, w: Int, h: Int): String = {
    val encodedString = new mutable.StringBuilder(w * h / 8)
    for (y:Int <- 0 to image.height.value.toInt-1) {
      for (x:Int <- 0 to image.width.value.toInt-1 by 4) {
        encodedString.append(encodeImageByte(image, x, y))
      }
    }

    "330190012c0100000000000000000000" + encodedString.toString()
  }

  def encodeImageByte(image: Image, x: Int, y:Int):Char = {
    def safeRead(x: Int, y: Int): Color = {
      if (y < image.height.value.toInt && y >= 0)
        image.pixelReader.get.getColor(x, y)
      else
        Color.Black
    }
    val p1:Int = (1-safeRead(x, y).grayscale.red).round.toInt
    val p2:Int = (1-safeRead(x+1, y).grayscale.red).round.toInt
    val p3:Int = (1-safeRead(x+2, y).grayscale.red).round.toInt
    val p4:Int = (1-safeRead(x+3, y).grayscale.red).round.toInt
    (p1*8 + p2*4 + p3*2 + p4).toHexString.charAt(0)
  }

  override val name: String = "Embedded Pico Systems E-Paper Display (Pixel Data Format Type 0) Encoder"
}
