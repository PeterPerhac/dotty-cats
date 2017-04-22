package exercises

import cats.Show


object StarCounting {

  type StarCount = Int

  implicit object StarShow extends Show[NightSky] {
    override def show(f: NightSky): String = f.rows.map(_.pixels.map {
      case Darkness => "#"
      case Star(_) => "-"
    }.mkString).mkString("\n")
  }

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

  val solver: NightSky => StarCount = nightSky => {
    println(Show[NightSky] show nightSky)
    println("...........................")
    def neighbouringStars(row: Int, col: Int): List[Star] = List(
      if (row > 0) Some(nightSky.rows(row - 1).pixels(col)) else None, //north
      if (col > 0) Some(nightSky.rows(row).pixels(col - 1)) else None, //west
      if (col < nightSky.w - 1) Some(nightSky.rows(row).pixels(col + 1)) else None, //east
      if (row < nightSky.h - 1) Some(nightSky.rows(row + 1).pixels(col)) else None //south
    ).flatten.collect {
      case s: Star => s
    }

    var newId = 0
    val stars = for {
      rowWithIndex <- nightSky.rows.zipWithIndex
      pixelWithIndex <- rowWithIndex._1.pixels.zipWithIndex if pixelWithIndex._1.isInstanceOf[Star]
    } yield {
      val (row, col) = rowWithIndex._2 -> pixelWithIndex._2
      val s = pixelWithIndex._1.asInstanceOf[Star]
      val stars = neighbouringStars(row, col) :+ s
      val topId = stars.foldLeft(newId + 1)((b, star) => star.id.fold(b)(identity))
      stars.foreach(_.id = Some(topId))
      newId = topId
      s
    }
    stars.flatMap(_.id).fold(0)(Math.max)
  }

}

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

final case class NightSky(w: Int, h: Int, rows: Seq[Row])