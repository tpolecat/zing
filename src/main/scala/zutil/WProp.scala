package zutil

import scalaz.syntax.monad._
import scalaz.concurrent.MVar
import scalaz.Contravariant
import scalaz.effect.IO

trait WProp[-A] { outer =>

  protected def priorCancel: MVar[Cancel]

  def put(a: => A): IO[Unit]

  def contramap[B](f: B => A): WProp[B] =
    new WProp[B] {
      protected def priorCancel = outer.priorCancel
      def put(b: => B): IO[Unit] = outer.put(f(b))
    }

  def -<-(p: RProp[A]): IO[Cancel] =
    for {
      x <- priorCancel.take
      _ <- x
      c <- p ->- (a => put(a))
      _ <- priorCancel.put(c)
    } yield c

  def -<-(a: A): IO[Cancel] =
    Prop(a) >>= (this -<- _)

}

object WProp {
  implicit object ContravariantWProp extends Contravariant[WProp] {
    def contramap[A, B](r: WProp[A])(f: B => A): WProp[B] = r contramap f
  }
}

