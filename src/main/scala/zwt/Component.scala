package zwt

import java.awt
import scalaz._
import Scalaz._
import scalaz.effect.IO
import zutil._
import java.awt.event.ComponentAdapter

abstract class Component(ps: Component.Props) {
  import Component._

  type Peer <: PeerUB
  def peer: Peer

  val bounds  = ps.bounds
  val visible = ps.visible
  val clicks  = ps.clicks

  val doubleClicks = ps.clicks.filter(_ == 2).map(_ => ())
  
}

object Component {

  type PeerUB = java.awt.Component

  trait Props {
    def bounds:  Prop[Rectangle]
    def visible: Prop[Boolean]
    def clicks:  RChan[Int]
  }

  object Props {
    def apply(c: PeerUB): IO[Props] =
      ^^(bounds(c), visible(c), clicks(c)) { (b, v, c) =>
        new Props {
          val bounds  = b
          val visible = v
          val clicks  = c
        }
      }
  }

  def bounds(c: PeerUB): IO[Prop[Rectangle]] =
    for {
      p <- Prop(Rectangle(c.getBounds))
      _ <- p ->- (_.toRectangle.map(c.setBounds))
      _ <- IO {
        c.addComponentListener(new awt.event.ComponentAdapter {
          val update: IO[Unit] = IO(c.getBounds).map(Rectangle(_)) >>= (p.put(_))
          override def componentResized(e: awt.event.ComponentEvent): Unit = update.unsafePerformIO
          override def componentMoved(e: awt.event.ComponentEvent): Unit = update.unsafePerformIO
        })
      }
    } yield p

  def visible(c: PeerUB): IO[Prop[Boolean]] =
    for {
      p <- Prop(c.isVisible)
      _ <- p ->- (IO(_).map(c.setVisible))
      _ <- IO {
        c.addComponentListener(new awt.event.ComponentAdapter {
          val update: IO[Unit] = IO(c.isVisible) >>= (p.put(_))
          override def componentShown(e: awt.event.ComponentEvent): Unit = update.unsafePerformIO
          override def componentHidden(e: awt.event.ComponentEvent): Unit = update.unsafePerformIO
        })
      }
    } yield p

  def clicks(c: PeerUB): IO[Chan[Int]] =
    for {
      p <- Chan[Int]
      _ <- IO {
        c.addMouseListener(new awt.event.MouseAdapter {
          override def mouseClicked(e: awt.event.MouseEvent): Unit =
            p.put(e.getClickCount).unsafePerformIO
        })
      }
    } yield p

}

