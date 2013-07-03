package zing

import scalaz.Scalaz._
import scalaz.effect.IO

import javax.swing.JFrame
import zwt.BorderLayout
import zwt.Frame
import zwt.LayoutManager
import zwt.Props

trait ZFrame extends Frame {
  type Peer <: JFrame
}

object ZFrame extends Props {

  def apply[L <: LayoutManager](layout: L): IO[ZFrame { type Layout = L }] =
    for {

      // Peer
      _peer <- IO {
        val f = new JFrame
        f.getContentPane().setLayout(layout.peer)
        f
      }

      // Props
      _bounds <- bounds(_peer)
      _visible <- visible(_peer)

    } yield new ZFrame {
      type Layout = L
      type Peer = JFrame
      val peer = _peer
      val bounds = _bounds
      val visible = _visible
    }

  def withBorderLayout: IO[ZFrame { type Layout = BorderLayout }] =
    BorderLayout() >>= apply _

}