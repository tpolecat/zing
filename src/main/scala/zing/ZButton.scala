package zing

import scalaz.syntax.monad._
import javax.swing.JButton
import scalaz.effect.IO
import scalaz.std.string._
import zutil.Prop
import zutil.RProp
import zwt.Component

abstract class ZButton(ps: ZButton.Props) extends ZComponent(ps.sup) {
  type Peer <: ZButton.PeerUB
  def text: Prop[String] = ps.text
}

object ZButton {

  type PeerUB = javax.swing.JButton

  def empty: IO[ZButton] =
    for {
      p <- IO(new JButton)
      ps <- Props(p)
    } yield new ZButton(ps) {
      type Peer = JButton
      val peer = p
    }

  trait Props {
    def sup: ZComponent.Props
    def text: Prop[String]
  }

  object Props {
    def apply(c: PeerUB): IO[Props] =
      ^(ZComponent.Props(c), text(c)) { (s, t) =>
        new Props {
          val sup = s
          val text = t
        }
      }
  }

  def text(a: PeerUB): IO[Prop[String]] =
    for {
      p <- Prop(a.getText)
      _ <- p ->- (IO(_).map(a.setText))
      // TODO: readable
    } yield p

}

