package zwt

import language.reflectiveCalls
import zutil.Prop
import scalaz.effect.IO
import java.awt
import scalaz._
import Scalaz._

trait Props {

  def bounds(c: java.awt.Component): IO[Prop[Rectangle]] = {
    for {
      p <- Prop(Rectangle(0, 0, 0, 0))
      _ <- IO {
        c.addComponentListener(new awt.event.ComponentAdapter {

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
//      _ <- p.listen(b => c.setBounds(b)) // todo
    } yield p
  }

  def visible(c: java.awt.Component): IO[Prop[Boolean]] = {
    for {
      p <- Prop(c.isVisible())
      _ <- IO {
        c.addComponentListener(new awt.event.ComponentAdapter {

          val update: IO[Unit] =
            IO(c.isVisible).map(p.put(_))

          override def componentShown(e: awt.event.ComponentEvent): Unit =
            update.unsafePerformIO

          override def componentHidden(e: awt.event.ComponentEvent): Unit =
            update.unsafePerformIO

        })
      }
      _ <- p ->- (b => IO(c.setVisible(b)))
    } yield p
  }

  def text(c: { def setText(s: String): Unit }): IO[Prop[String]] = {
    for {
      p <- Prop("")
      _ <- p ->- (a => IO(c.setText(a)))
    } yield p
  }

}