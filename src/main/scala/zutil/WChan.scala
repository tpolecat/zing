package zutil

import scalaz.syntax.monad._
import scalaz.concurrent.MVar
import scalaz.Contravariant
import scalaz.effect.IO
import scalaz.Equal

trait WChan[-A] { outer =>

  protected def priorCancel: MVar[Cancel]

  def put(a: => A): IO[Unit]

  def contramap[B](f: B => A): WChan[B] =
    new WChan[B] {
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

  def -<-[B <: A](a: B)(implicit ev: Equal[B]): IO[Cancel] =
    Prop(a) >>= (this -<- _)

}

object WChan {
  implicit object ContravariantWChan extends Contravariant[WChan] {
    def contramap[A, B](r: WChan[A])(f: B => A): WChan[B] = r contramap f
  }
}

