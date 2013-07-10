package zutil

import scalaz.effect.IO

trait Controller[A] {
  type Ix
}

trait LinearController[A] extends Controller[A] {
  
  def list: Prop[Vector[A]]
  def zipWithIx: Prop[Vector[(A, Ix)]]  
  def map: Prop[Map[Ix, A]]
  
  def clear: IO[Unit]
  def delete(i: Ix): IO[Boolean]
  def insert(before: Ix, a:A): IO[Option[Ix]]
  def append(a:A): IO[Ix]

  def get(i: Ix): IO[Option[A]]
  def put(i: Ix, a:A): IO[Option[Unit]]
  
}

trait Selection {
  type Ix
  
  def select(i: Ix): IO[Boolean]
  def deselect(i: Ix): IO[Boolean]
  
}

trait SingleSelection extends Selection {  
  def selection: Prop[Ix]
}

trait MultipleSelection extends Selection {  
  def selection: Prop[List[Ix]]
}

trait SingleSelectionZList[A] {
  
  def controller: LinearController[A]
  
  def selection: SingleSelection
  
  
  
}