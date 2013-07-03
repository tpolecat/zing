package zwt

import java.awt
import scalaz.effect.IO

trait Container extends Component {

  type Layout <: LayoutManager
  type Peer <: awt.Container
  
  def addComponent(constraint: Layout#Constraint)(comp: Component): IO[Unit] =
    IO(peer.add(comp.peer, constraint.value))
      
  def removeComponent(comp: Component): IO[Unit] =
    IO(peer.remove(comp.peer))
      
}


