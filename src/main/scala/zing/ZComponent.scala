package zing

 
import zwt.Component
import scalaz.effect.IO

// For JComponents that are not meaningful containers
abstract class ZComponent(ps: ZComponent.Props) extends Component(ps.sup) {
	type Peer <: ZComponent.PeerUB
}


object ZComponent {
  
  type PeerUB = javax.swing.JComponent

  trait Props {
    def sup: Component.Props
  }
    
  object Props {
    def apply(c: PeerUB): IO[Props] =
      Component.Props(c) map { s =>
        new Props {
          val sup = s
        }
      }
  }  

}