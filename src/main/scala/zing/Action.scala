package zing

trait Action {
  type Peer <: javax.swing.Action
  def peer: Peer
}

