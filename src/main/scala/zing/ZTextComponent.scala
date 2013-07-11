package zing

import scalaz.syntax.monad._
import javax.swing.JLabel
import scalaz.effect.IO
import scalaz.effect.IO._
import scalaz.std.string._
import zutil.Prop
import zutil.RProp
import zwt.Component
import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent

abstract class ZTextComponent(ps: ZTextComponent.Props) extends ZComponent(ps.sup) {
  type Peer <: ZTextComponent.PeerUB
  val text = ps.text
}

object ZTextComponent {

  type PeerUB = javax.swing.text.JTextComponent

  trait Props {
    def sup: ZComponent.Props
    def text: Prop[String]
  }

  object Props {

    def apply(c: PeerUB): IO[Props] =
      ^(ZComponent.Props(c), text(c)) { (s, c) =>
        new Props {
          val sup = s
          val text = c
        }
      }

    def text(c: PeerUB): IO[Prop[String]] =
      for {
        p <- Prop(c.getText)
        _ <- p ->- (s => IO(if (c.getText != s) c.setText(s)))
        _ <- IO {
          c.getDocument.addDocumentListener(new DocumentListener {
            def update() = IO(c.getText) >>= (p := _)
            def changedUpdate(e: DocumentEvent): Unit = update.unsafePerformIO()
            def insertUpdate(e: DocumentEvent): Unit = update.unsafePerformIO()
            def removeUpdate(e: DocumentEvent): Unit = update.unsafePerformIO()
          })
        }
      } yield p
  }

}