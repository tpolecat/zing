package zwt

import java.awt
import scalaz.effect.IO

abstract class Window(props: Window.Props) extends Container(props.sup) {
  type Peer <: Window.PeerUB
}

object Window {
  
  type PeerUB = awt.Window
   
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