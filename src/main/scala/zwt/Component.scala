package zwt

import java.awt
import scalaz.effect.IO
import zutil.Prop
import java.awt.event.ComponentAdapter

trait Component {

  type Peer <: awt.Component
  def peer: Peer

  def bounds: Prop[Rectangle]
  def visible: Prop[Boolean]
  
}

object Component {

  def bounds(c: awt.Component): IO[Prop[Rectangle]] = {
    for {
      p <- Prop(Rectangle(0, 0, 0, 0))
      _ <- IO {
        c.addComponentListener(new ComponentAdapter {

          val update: IO[Unit] =
            for {
              b <- IO(c.getBounds)
              _ <- p.put(Rectangle(b.getX, b.getY, b.getWidth, b.getHeight))
            } yield ()

          override def componentResized(e: awt.event.ComponentEvent): Unit = 
            update.unsafePerformIO

          override def componentMoved(e: awt.event.ComponentEvent): Unit = 
            update.unsafePerformIO
            
        })
      }
    } yield p
  }

}

