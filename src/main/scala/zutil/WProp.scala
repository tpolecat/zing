package zutil

import scalaz.syntax.monad._
import scalaz.concurrent.MVar
import scalaz.Contravariant
import scalaz.effect.IO
import scalaz.Equal

trait WProp[-A] { outer =>

  protected def priorCancel: MVar[Cancel]

  def :=(a: => A): IO[Unit]

  def contramap[B](f: B => A): WProp[B] =
    new WProp[B] {
      protected def priorCancel = outer.priorCancel
      def :=(b: => B): IO[Unit] = outer := f(b)
    }

  def -<-(p: RProp[A]): IO[Cancel] =
    for {
      x <- priorCancel.take
      _ <- x
      c <- p ->- (this := _)
      _ <- priorCancel.put(c)
    } yield c

}

object WProp {
  implicit object ContravariantWProp extends Contravariant[WProp] {
    def contramap[A, B](r: WProp[A])(f: B => A): WProp[B] = r contramap f
  }
}

