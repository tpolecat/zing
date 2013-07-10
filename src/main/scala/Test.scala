
import java.util.Date

import scalaz.Scalaz._
import scalaz.effect.IO
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.putStrLn
import scalaz.effect.SafeApp

import zing.ZFrame
import zing.ZLabel
import zutil.Clock
import zwt.BorderLayout
import zwt.Component

object Test extends SafeApp {

  override def runc: IO[Unit] =
    for {
      f <- ZFrame.withBorderLayout
      _ <- clockLabel >>= f.addComponent(BorderLayout.North)
      _ <- boundsLabel(f) >>= f.addComponent(BorderLayout.South)
      _ <- f.visible.map("Visible? " + _) ->- putStrLn
      _ <- f.visible -<- true
    } yield ()

  def clockLabel: IO[ZLabel] =
    for {
      c <- Clock.withResolution(500)
      l <- ZLabel.empty
      _ <- l.text -<- c.map(new Date(_).toString)
      _ <- l.doubleClicks ->- putStrLn("Double-click!")
    } yield l

  def boundsLabel(c: Component): IO[ZLabel] =
    for {
      l <- ZLabel.empty
      _ <- l.text -<- c.bounds.map(_.toString)
    } yield l

}

