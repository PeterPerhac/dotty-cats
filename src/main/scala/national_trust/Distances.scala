package national_trust

import java.lang.Math.{atan2, cos, sin, sqrt}
import java.nio.file.{Files, Paths}

import play.api.libs.json.{Json, OFormat}

object Distances {

  case class Place(name: String, lat: Double, lon: Double)

  object Place {
    implicit val format: OFormat[Place] = Json.format
  }

  def latLonDistance(lat1: Double, lon1: Double)(lat2: Double, lon2: Double): Double = {
    val earthRadiusKm = 6371
    val dLat = (lat2 - lat1).toRadians
    val dLon = (lon2 - lon1).toRadians
    val latRad1 = lat1.toRadians
    val latRad2 = lat2.toRadians

    val a = sin(dLat / 2) * sin(dLat / 2) + sin(dLon / 2) * sin(dLon / 2) * cos(latRad1) * cos(latRad2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    earthRadiusKm * c
  }

  implicit class MilesConverter(val km: Double) {
    def toMiles: Double = km / 1.60934
  }

  def main(args: Array[String]): Unit = {
    val p = Paths.get("/Users/peterperhac/my/private-area/national-trust/nt-places-with-coordinates.json")
    val json = Json.parse(Files.newInputStream(p))
    // 50.726242,-3.4730317 //holiday inn exeter
    // 50.835256, -0.195984 //home
    val distanceFromHome = latLonDistance(50.726242,-3.4730317) _
    implicit val doubleOrdering = Ordering.Double.IeeeOrdering
    json.validate[List[Place]]
      .getOrElse(List.empty[Place])
      .map(p => p -> distanceFromHome(p.lat, p.lon))
      .sortBy {
        case (place, distance) => distance
      }
      .foreach {
        case (Place(name, _, _), km) =>
          println(f"$name%s\t$km%2.1f km\t${km.toMiles}%2.1f mi")
      }
  }

}
