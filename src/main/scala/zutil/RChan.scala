package zutil

import scalaz._
import Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioUnit
import scalaz.Functor

trait RChan[+A] { o =>

  def map[B](f: A => B): RChan[B] =
    new RChan[B] {
      def ->-(g: B => IO[Unit]): IO[Cancel] =
        o ->- (a => g(f(a)))
    }

  def filter(f: A => Boolean): RChan[A] =
    new RChan[A] {
      def ->-(g: A => IO[Unit]): IO[Cancel] =
        o ->- (a => f(a) ? g(a) | ioUnit)
    }

  def ->-(f: A => IO[Unit]): IO[Cancel]

  def ->-(f: IO[Unit]): IO[Cancel] =
    ->- (_ => f)

  
}

object RChan {
  implicit object FunctorRChan extends Functor[RChan] {
    override def map[A, B](fa: RChan[A])(f: A => B): RChan[B] = fa map f
  }
}
