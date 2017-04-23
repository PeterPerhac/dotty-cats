package exercises

import cats.Show

import scala.collection.mutable


sealed trait Pixel

case object Darkness extends Pixel

final case class Star(var id: Option[Int] = None) extends Pixel

final case class Row(pixels: Seq[Pixel])

object Row {
  def fromString(s: String) = Row(s map {
    case '#' => Darkness
    case '-' => Star()
    case _ => throw new RuntimeException("UFO!")
  })

}

final case class StarWithLocation(star: Star, row: Int, col: Int)


object NightSky {

  implicit val starShow = Show.show {
    (ns: NightSky) =>
      ns.rows.map(_.pixels.map {
        case Darkness => "#"
        case Star(id) => id.fold("-")(_.toString)
      }.mkString).mkString("\n")
  }

}

final case class NightSky(w: Int, h: Int, rows: Seq[Row]) {

  val allStars: Seq[StarWithLocation] =
    for {
      (row, ridx) <- rows.zipWithIndex
      (star, cidx) <- row.pixels.zipWithIndex if star.isInstanceOf[Star]
    } yield {
      StarWithLocation(star.asInstanceOf[Star], ridx, cidx)
    }

  def starAt(x: Int, y: Int): Option[Star] = rows(y).pixels(x) match {
    case Darkness => None
    case s@Star(id) => Some(s)
  }

  def identifyStar(x: Int, y: Int, id: Int): Unit = starAt(x, y).foreach(_.id = Some(id))

  override def toString: String = Show[NightSky] show this

}


object StarCounting {

  type StarCount = Int

  def main(args: Array[String]): Unit = {
    args.headOption.foreach {
      file =>
        readImages(scala.io.Source.fromFile(file).getLines.toList).map(solver).zipWithIndex.foreach {
          case (starCount, index) => println(s"Case ${index + 1}: $starCount")
        }
    }
  }

  def readImages(lines: => List[String]): Seq[NightSky] = {
    val DimensionsPattern = """^(\d+) (\d+)$""".r

    def readNightSky(ls: List[String], acc: Seq[NightSky]): Seq[NightSky] = ls match {
      case DimensionsPattern(rows, columns) :: tail =>
        val (w, h) = (columns.toInt, rows.toInt)
        val (rs, rest) = tail splitAt h
        readNightSky(rest, acc :+ NightSky(w, h, rs map Row.fromString))
      case _ => acc
    }

    readNightSky(lines, Seq.empty)
  }

  def floodFill(ns: NightSky, x: Int, y: Int, id: Int): Int = {

    if (ns.starAt(x, y).flatMap(_.id).fold(false)(_ == id)) return id + 1

    val oldId = ns.starAt(x, y).flatMap(_.id)
    val pixels = new mutable.Stack[(Int, Int)]

    def paint(fx: Int, fy: Int) = ns.identifyStar(fx, fy, id)

    def old(cx: Int, cy: Int): Boolean = ns.starAt(cx, cy).fold(false)(_.id == oldId)

    def push(px: Int, py: Int) = pixels.push((px, py))

    // starting point
    push(x, y)

    while (pixels.nonEmpty) {
      val (x, y) = pixels.pop()
      var y1 = y
      while (y1 >= 0 && old(x, y1)) y1 -= 1
      y1 += 1
      var spanLeft = false
      var spanRight = false
      while (y1 < ns.h && old(x, y1)) {
        paint(x, y1)
        if (x > 0 && spanLeft != old(x - 1, y1)) {
          if (old(x - 1, y1)) push(x - 1, y1)
          spanLeft = !spanLeft
        }
        if (x < ns.w - 1 && spanRight != old(x + 1, y1)) {
          if (old(x + 1, y1)) push(x + 1, y1)
          spanRight = !spanRight
        }
        y1 += 1
      }
    }

    id + 1
  }

  val solver: NightSky => StarCount = nightSky => {

    val unidentifiedStar: StarWithLocation => Boolean = _.star.id.isEmpty
    var newId = 1

    while (nightSky.allStars.exists(unidentifiedStar)) {
      nightSky.allStars.find(unidentifiedStar).foreach(swl =>
        newId = floodFill(nightSky, swl.col, swl.row, newId))
    }
    println(Show[NightSky] show nightSky)
    println("...........................")
    nightSky.allStars.flatMap(_.star.id).distinct.size
  }

}
