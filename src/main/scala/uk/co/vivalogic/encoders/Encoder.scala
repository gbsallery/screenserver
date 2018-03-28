package uk.co.vivalogic.encoders

import scalafx.scene.image.Image

/**
 * Created by gavin on 29/05/15.
 */
trait Encoder {
  def convertImage(image: Image, w: Int, h: Int): String
  val name: String
}
