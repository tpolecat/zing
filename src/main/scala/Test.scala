
import java.util.Date
import scalaz.Scalaz._
import scalaz.effect._
import scalaz.concurrent.MVar._
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.putStrLn
import scalaz.effect.SafeApp
import zing.ZFrame
import zing.ZLabel
import zutil.Clock
import zwt.BorderLayout
import zwt.Component
import zing.ZButton

object Test extends SafeApp {

  override def runc: IO[Unit] =
    for {
      f <- ZFrame.withBorderLayout
      _ <- clockLabel     >>= f.addComponent(BorderLayout.North)
      _ <- button         >>= f.addComponent(BorderLayout.Center)
      _ <- boundsLabel(f) >>= f.addComponent(BorderLayout.South)
      _ <- f.visible.map("Visible? " + _) ->- putStrLn
      _ <- f.visible := true
    } yield ()

  def clockLabel: IO[ZLabel] =
    for {
      c <- Clock.withResolution(500)
      l <- ZLabel.empty
      _ <- l.text -<- c.map(new Date(_).toString) // works backwards if you want
      _ <- l.doubleClicks ->- putStrLn("Double-click!")
    } yield l

  def boundsLabel(c: Component): IO[ZLabel] =
    for {
      l <- ZLabel.empty
      _ <- l.text -<- c.bounds.map(_.toString)
    } yield l

  def button: IO[ZButton] =
    for {
      b <- ZButton.empty
      _ <- b.text := "Click Me!"
      c <- newMVar(0)
      _ <- b.clicks ->- {
        for {
          n <- c.modify(n => IO((n + 1, n + 1)))
          _ <- b.text := s"Counted $n clicks."
        } yield ()
      }
    } yield b

}

