package zing

import javax.swing.JButton
import scalaz.effect.IO
import zutil.RProp
import zwt.Component
import zwt.Props

trait ZButton extends ZAbstractButton {
  type Peer <: JButton
}

object ZButton extends Props {

  def apply(caption: String): IO[ZButton] =
    for {
      p <- IO(new JButton(caption))
      bp <- bounds(p)
      
      _visible <- visible(p)
      
      b <- IO {
        new ZButton {
          type Peer = JButton
          val peer = p
          val bounds = bp
          val visible = _visible
        }
      }
    } yield b

}

