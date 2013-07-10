package zing

import zwt.Container
import scalaz.effect.IO

// For JComponents that are containers
abstract class ZContainer(ps: ZContainer.Props) extends Container(ps.sup) {
  type Peer <: ZComponent.PeerUB
}

object ZContainer {

  type PeerUB = javax.swing.JComponent

  trait Props {
    def sup: Container.Props
  }

  object Props {
    def apply(c: PeerUB): IO[Props] =
      Container.Props(c) map { s =>
        new Props {
          val sup = s
        }
      }
  }

}