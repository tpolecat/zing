package zwt

import java.awt
import scalaz.effect.IO

trait Window extends Container {
  type Peer <: awt.Container
}