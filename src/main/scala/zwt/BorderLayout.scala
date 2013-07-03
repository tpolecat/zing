package zwt

import scalaz.effect.IO

sealed trait BorderLayout extends LayoutManager {
  type Peer <: java.awt.BorderLayout
  type Constraint = BorderLayout.Constraint
}

object BorderLayout {

  def apply(): IO[BorderLayout] = IO {
    new BorderLayout {
      type Peer = java.awt.BorderLayout
      def peer = new java.awt.BorderLayout
    }
  }

  sealed abstract class Constraint(s: String) extends LayoutManager.Constraint {
    type WrappedType = String
    def value: WrappedType = s
  }

  case object North extends Constraint(java.awt.BorderLayout.NORTH)
  case object South extends Constraint(java.awt.BorderLayout.SOUTH)
  case object East extends Constraint(java.awt.BorderLayout.EAST)
  case object West extends Constraint(java.awt.BorderLayout.WEST)
  case object Center extends Constraint(java.awt.BorderLayout.CENTER)

}

