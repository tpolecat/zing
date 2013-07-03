package zwt

abstract class LayoutManager {
  type Peer <: java.awt.LayoutManager
  type Constraint <: LayoutManager.Constraint
  def peer: Peer
}

object LayoutManager {

  trait Constraint {
  	type WrappedType
    def value: WrappedType
  }

}