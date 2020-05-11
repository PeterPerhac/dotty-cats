package scratches
import java.nio.file.{Files, Paths, StandardOpenOption}

import scala.PartialFunction.condOpt
import scala.util.Random

object Fractals extends App {

  case class Vector(x: Double, y: Double) {
    def times(factor: Double): Vector = Vector(x * factor, y * factor)
  }
  case class Point(x: Int, y: Int) {
    def to(p2: Point): Vector = Vector(p2.x - this.x, p2.y - this.y)
    def add(v: Vector): Point = Point(this.x + v.x.toInt, this.y + v.y.toInt)
    def toPixel(color: Double): String =
      s"pixel($x, $y, ${((0.40 + (0.5 * color)) * 255).toInt});"
  }

  val W = 600

  val star: Array[Point] = Array(
    Point(300, 10),
    Point(15, 217),
    Point(124, 553),
    Point(476, 553),
    Point(585, 217)
  )

  val randomPoint: Int => (Point, Int) = prevIdx => {
    val rIdx = LazyList
      .unfold(prevIdx) { last =>
        val res = Random.nextInt(5);
        condOpt(res) { case n if last == prevIdx => n -> n }
      }
      .last
    star(rIdx) -> rIdx
  }

  val partWay: Double => Point => Point => Point =
    d => start => end => start.add(start.to(end).times(d))
  val halfWay: Point => Point => Point = partWay(0.5)

  val points = LazyList.unfold((star(0), 0)) {
    case (p, vIdx) =>
      val (nextPoint, idx) = randomPoint(vIdx)
      val halfWayPoint = halfWay(p)(nextPoint)
      Some((halfWayPoint, halfWayPoint -> idx))
  }

  val POINTS = 75000
  val pixels = points.zipWithIndex
    .map {
      case (point, i) => point.toPixel(i.doubleValue() / POINTS)
    }
    .take(POINTS)

  val w = Files
    .newBufferedWriter(
      Paths.get("/Users/peterperhac/my/javascript/raphael/fractals/index.html"),
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )

  w.write(s"""
             |<!DOCTYPE html>
             |<html>
             |<head>
             |  <meta name="viewport" content="initial-scale=1">
             |	<title>Drawing fractals with Raphael</title>
             |  <script src="raphael.js"></script>
             |</head>
             |<body>
             |    <div id="canvas"></div>
             |	<footer>
             |        <section>
             |          Peter Perhac, 11 May 2020
             |          Implementation of https://twitter.com/CentrlPotential/status/1250172108811927552?s=20
             |        </section>
             |    </footer>
             |  <script>
             |    var paper = Raphael("canvas", $W, $W);
             |    paper.canvas.style.backgroundColor = '#000';
             |    var pixel = function(x,y,c){
             |      paper.rect(x,y,1,1).attr({"stroke": "none", "fill": this.paper.raphael.rgb(0,c,0)});
             |    }
             |
             |   ${pixels.mkString("\n")}
             |
             |  </script>
             |</body>
             |</html>
             |
             |""".stripMargin)

  w.flush()

}
