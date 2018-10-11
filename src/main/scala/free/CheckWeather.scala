package free

import cats.effect.Sync
import cats.free.Free
import cats.free.Free.liftF
import cats.~>
import monix.eval.Coeval

final case class WeatherInfo(location: String, minTemp: Double, maxTemp: Double)

sealed trait WeatherOperation[T]

case object GetLocation extends WeatherOperation[String]

final case class QueryWeatherSource(location: String) extends WeatherOperation[WeatherInfo]

final case class OutputWeatherInfo(weatherInfo: WeatherInfo) extends WeatherOperation[Unit]

trait WeatherService {
  type WeatherOp[A] = Free[WeatherOperation, A]

  val askLocation: WeatherOp[String] = liftF(GetLocation)

  def queryWeatherSource(location: String): WeatherOp[WeatherInfo] = liftF(QueryWeatherSource(location))

  def output(weatherInfo: WeatherInfo): WeatherOp[Unit] = liftF(OutputWeatherInfo(weatherInfo))
}

abstract class Interpreter[F[_] : Sync] extends ~>[WeatherOperation, F] {
  def S: Sync[F] = implicitly
}

class DummyInterpreter[F[_] : Sync] extends Interpreter[F] {

  override def apply[A](fa: WeatherOperation[A]): F[A] = fa match {
    case GetLocation => S.delay {
      println("Please enter Location:")
      scala.io.StdIn.readLine()
    }
    case QueryWeatherSource(location) => S.delay {
      WeatherInfo(location, location.length, location.length * 2)
    }
    case OutputWeatherInfo(wi) => S.delay {
      println(s"Weather in ${wi.location}: temperatures between ${wi.minTemp} - ${wi.maxTemp}")
    }
  }
}


object CheckWeather extends WeatherService {

  def main(args: Array[String]): Unit = {
    val program = for {
      location <- askLocation
      weatherInfo <- queryWeatherSource(location)
      _ <- output(weatherInfo)
    } yield ()

    program.foldMap(new DummyInterpreter[Coeval]).run
  }

}
