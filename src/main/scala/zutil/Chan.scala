package zutil

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioUnit
import scalaz.concurrent.MVar
import scalaz.concurrent.MVar.newMVar

/** A read-write channel. */
trait Chan[A] extends RChan[A] with WChan[A] {
  def rchan: RChan[A] = this
  def wchan: WChan[A] = this
}

object Chan {

  def apply[A: Equal]: IO[Chan[A]] =
    for {
      listeners <- newMVar[List[A => IO[Unit]]](Nil)
      pc <- newMVar(ioUnit)
    } yield new Chan[A] {

      protected def priorCancel: MVar[Cancel] =
        pc

      def put(a: => A): IO[Unit] =
        for {
          l <- listeners.read
          _ <- l.traverseU(_(a))
        } yield ()

      def ->-(g: A => IO[Unit]): IO[Cancel] =
        for {
          _ <- listeners.modify(ls => IO((g :: ls, ())))
        } yield listeners.modify(ls => IO((ls.filterNot(_ == g), ())))

    }

}
