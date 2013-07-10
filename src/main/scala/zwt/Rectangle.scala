package zwt

import scalaz.Equal
import scalaz.effect.IO

case class Rectangle(x: Double, y: Double, w: Double, h: Double) {
  def toRectangle: IO[java.awt.Rectangle] = IO {
    val r = new java.awt.Rectangle()
    r.setRect(x, y, w, h)
    r
  }
}

object Rectangle {

  implicit val equal = Equal.equalA[Rectangle]
 
  def apply(r: java.awt.Rectangle): Rectangle =
    Rectangle(r.getX, r.getY, r.getWidth, r.getHeight)
  
}