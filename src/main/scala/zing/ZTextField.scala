package zing

import scalaz.syntax.monad._
import javax.swing.JButton
import scalaz.effect.IO
import scalaz.std.string._
import zutil.Prop
import zutil.RProp
import zwt.Component
import javax.swing.JTextField

abstract class ZTextField(ps: ZTextField.Props) extends ZTextComponent(ps.sup) {
  type Peer <: ZTextField.PeerUB
}

object ZTextField {

  type PeerUB = javax.swing.JTextField

  def empty: IO[ZTextField] =
    for {
      p <- IO(new JTextField)
      ps <- Props(p)
    } yield new ZTextField(ps) {
      type Peer = JTextField
      val peer = p
    }

  trait Props {
    def sup: ZTextComponent.Props
  }

  object Props {
    def apply(c: PeerUB): IO[Props] =
      ZTextComponent.Props(c) map { s =>
        new Props {
          val sup = s
        }
      }
  }

}

