import scalaz.effect.IO

package object zutil {

  type Cancel = IO[Unit]

}