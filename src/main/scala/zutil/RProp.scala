package zutil

import scalaz.syntax.apply._
import scalaz.Apply
import scalaz.effect.IO

trait RProp[+A] { o =>

  def get: IO[A]

  def map[B](f: A => B): RProp[B] =
    new RProp[B] {

      def get: IO[B] =
        o.get.map(f)

      def ->-(g: B => IO[Unit]): IO[Cancel] =
        o ->- (a => g(f(a)))

    }

  def ap[B](f: RProp[A => B]): RProp[B] =
    new RProp[B] {

      def get: IO[B] =
        o.get.flatMap(a => f.get.map(_(a)))

      def ->-(g: B => IO[Unit]): IO[Cancel] =
        for {
          x <- o ->- (a => f.get.map(_.apply(a)).flatMap(g))
          y <- f ->- (f => o.get.map(f).flatMap(g))
        } yield (x |@| y)((_, _) => ())

    }

  def ->-(f: A => IO[Unit]): IO[Cancel]

}

object RProp {
  implicit object ApplyRProp extends Apply[RProp] {
    override def map[A, B](fa: RProp[A])(f: A => B): RProp[B] = fa map f
    def ap[A, B](fa: => RProp[A])(f: => RProp[A => B]): RProp[B] = fa ap f
  }
}
