package zutil

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioUnit
import scalaz.concurrent.MVar
import scalaz.concurrent.MVar.newMVar

/** A read-write property. A property always has a value (contrast with a Channel). */
trait Prop[A] extends RProp[A] with WProp[A] {
  def rprop: RProp[A] = this
  def wprop: WProp[A] = this
}

object Prop {

  def apply[A](a: A): IO[Prop[A]] =
    for {
      listeners <- newMVar[List[A => IO[Unit]]](Nil)
      value <- newMVar(a)
      pc <- newMVar(ioUnit)
    } yield {
      new Prop[A] {

        protected def priorCancel: MVar[Cancel] =
          pc

        def get: IO[A] =
          value.read

        def put(a: => A): IO[Unit] =
          for {
            _ <- value.take
            _ <- value.put(a)
            l <- listeners.read
            _ <- l.traverseU(_(a))
          } yield ()

        def ->-(g: A => IO[Unit]): IO[Cancel] =
          for {
            _ <- listeners.modify(ls => IO((g :: ls, ())))
            _ <- value.read >>= g
          } yield listeners.modify(ls => IO((ls.filterNot(_ == g), ())))

      }
    }

}
