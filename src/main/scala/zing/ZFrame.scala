package zing

import scalaz.Scalaz._
import scalaz.effect.IO

import javax.swing.JFrame
import zwt.BorderLayout
import zwt.Frame
import zwt.LayoutManager

abstract class ZFrame(ps: ZFrame.Props) extends Frame(ps.sup) {
  type Peer <: ZFrame.PeerUB
}

object ZFrame {

  def apply[L <: LayoutManager](layout: L): IO[ZFrame { type Layout = L }] =
    for {

      p <- IO {
        val f = new JFrame
        f.getContentPane().setLayout(layout.peer)
        f
      }

      ps <- Props(p)

    } yield new ZFrame(ps) {
      type Layout = L
      type Peer = JFrame
      val peer = p
    }

  def withBorderLayout: IO[ZFrame { type Layout = BorderLayout }] =
    BorderLayout() >>= apply _

  type PeerUB = JFrame

  trait Props {
    def sup: Frame.Props
  }

  object Props {
    def apply(c: PeerUB): IO[Props] =
      Frame.Props(c) map { s =>
        new Props {
          val sup = s
        }
      }
  }

}

