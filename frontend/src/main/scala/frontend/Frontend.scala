package frontend

import scala.concurrent.duration._

import cats._
import cats.implicits._
import cats.syntax._
import cats.effect._

import monix.reactive._
import monix.execution.Scheduler.Implicits.global

import outwatch.dom._
import outwatch.dom.dsl._

object Frontend extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    OutWatch
      .renderInto("#app", app)
      .handleError { t =>
        println("Error: " + t.getMessage())
      } *> IO(ExitCode.Success)
  }

  val secondsObservable: Observable[Long] =
    Observable.interval(1.second)

  val app: VNode = div(
    secondsObservable.map(
      t => s"Hello world since $t seconds!"
    )
  )
}
