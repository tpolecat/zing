package zwt

import java.awt
import scalaz.effect.IO

abstract class Frame(ps: Frame.Props) extends Window(ps.sup) {
	type Peer <: Frame.PeerUB
}

object Frame {
  
  type PeerUB = awt.Frame

  trait Props {
    def sup: Window.Props
  }
    
  object Props {
    def apply(c: PeerUB): IO[Props] =
      Window.Props(c) map { s =>
        new Props {
          val sup = s
        }
      }
  }  

}