package zutil

import java.util.{Timer, TimerTask}
import scalaz.effect.IO
import scalaz._
import Scalaz._

object Clock {

  private val t = new Timer

  def withResolution(ms: Long): IO[RProp[Long]] =
    Prop(0L).map { p =>
      val update = IO(System.currentTimeMillis) >>= (p := _)
      t.scheduleAtFixedRate(new TimerTask {
        def run = update.unsafePerformIO
      }, 0L, ms)
      p
    }

}