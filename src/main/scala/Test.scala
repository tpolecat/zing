
import java.util.Date
import scalaz.Scalaz._
import scalaz.effect._
import scalaz.concurrent.MVar
import scalaz.concurrent.MVar._
import scalaz.effect.IO.ioMonadCatchIO
import scalaz.effect.IO.putStrLn
import scalaz.effect.SafeApp
import zing.ZFrame
import zing.ZLabel
import zutil.Clock
import zwt.BorderLayout
import zwt.Component
import zing._
import zutil.Prop

object Test extends SafeApp {

  implicit class MVarOps[A](m: MVar[A]) {
    def mod(f: A => A): IO[A] =
      m.modify(a => IO(f(a).squared))
  }
  
  override def runc: IO[Unit] =
    for {
      f <- ZFrame.withBorderLayout
      t <- text
      _ <- clockLabel >>= f.addComponent(BorderLayout.North)
      _ <- button >>= f.addComponent(BorderLayout.West)
      _ <- boundsLabel(f) >>= f.addComponent(BorderLayout.South)
      _ <- f.addComponent(BorderLayout.Center)(t)
      _ <- stats(t.text) >>= f.addComponent(BorderLayout.East)
      _ <- f.visible.map("Visible? " + _) ->- putStrLn
      _ <- f.visible := true
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

  def button: IO[ZButton] =
    for {
      b <- ZButton.empty
      _ <- b.text := "Click Me!"
      c <- newMVar(0)
      _ <- b.clicks ->- {
        for {
          n <- c.mod(_ + 1)
          _ <- b.text := s"Counted $n clicks."
        } yield ()
      }
    } yield b

  def text: IO[ZTextField] =
    for {
      t <- ZTextField.empty
      _ <- t.text := "foo"
      _ <- t.text.map("Text: " + _) ->- putStrLn
    } yield t

  def stats(t: Prop[String]): IO[ZLabel] =
    for {
      l <- ZLabel.empty
      _ <- l.text -<- t.map(_.length).map(_ + " chars")
    } yield l

}

