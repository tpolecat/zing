package zwt

import java.awt
import scalaz.effect.IO

abstract class Container(ps: Container.Props) extends Component(ps.sup) {

  type Layout <: LayoutManager
  type Peer <: Container.PeerUB
  
  def addComponent(constraint: Layout#Constraint)(comp: Component): IO[Unit] =
    IO(peer.add(comp.peer, constraint.value))
      
  def removeComponent(comp: Component): IO[Unit] =
    IO(peer.remove(comp.peer))
      
}

object Container {
  
  type PeerUB = awt.Container
 
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