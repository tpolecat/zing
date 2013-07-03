package zing

import scalaz.syntax.monad._
import javax.swing.JLabel
import scalaz.effect.IO
import zutil.Prop
import zutil.RProp
import zwt.Component
import zwt.Props

trait ZLabel extends ZComponent {
  type Peer <: JLabel
  def text: Prop[String]
}

object ZLabel extends Props {

  def empty: IO[ZLabel] =
    for {

      // Our component
      p <- IO(new JLabel)

      // Properties
      pText <- text(p)
      pBounds <- bounds(p)
      pVisible <- visible(p)

    } yield new ZLabel {
      type Peer = JLabel
      val peer = p
      val text = pText
      val bounds = pBounds
      val visible = pVisible
    }

}

