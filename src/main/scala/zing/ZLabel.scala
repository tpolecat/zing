package zing

import scalaz.syntax.monad._
import javax.swing.JLabel
import scalaz.effect.IO
import scalaz.std.string._
import zutil.Prop
import zutil.RProp
import zwt.Component

abstract class ZLabel(ps: ZLabel.Props) extends ZComponent(ps.sup) {
  type Peer <: ZLabel.PeerUB
  def text: Prop[String] = ps.text
}

object ZLabel {

  type PeerUB = javax.swing.JLabel

  def empty: IO[ZLabel] =
    for {
      p <- IO(new JLabel)
      ps <- Props(p)
    } yield new ZLabel(ps) {
      type Peer = JLabel
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

