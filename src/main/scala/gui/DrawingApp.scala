package gui

import java.awt.Color
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

import scala.swing._
import scala.swing.event.{Key, KeyPressed}
import scala.util.Random

object DrawingApp extends SimpleSwingApplication {

  val W = 600
  val H = 600
  val r = 298

  val canvas = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB)

  class DataPanel(frame: MainFrame) extends Panel {

    def myRefresh(): Unit = {
      val g = canvas.createGraphics()
      g.setBackground(Color.BLACK)
      g.setColor(Color.BLACK)
      g.fill(new Rectangle2D.Float(0f, 0f, W.toFloat, H.toFloat))
      g.dispose()
      plotPoints()
      frame.title = f"distance = $distance%.2f"
      this.repaint()
    }

    listenTo(keys)
    focusable = true
    requestFocusInWindow()

    reactions += {
      case KeyPressed(_, Key.Up, _, _) =>
        distance = distance + 0.01d
        myRefresh()
      case KeyPressed(_, Key.Down, _, _) =>
        distance = distance - 0.01d
        myRefresh()
    }

    def plotPoints(): Unit =
      LazyList
        .unfold((polygon(0), 0)) {
          case (p, vIdx) =>
            val (nextPoint, idx) = randomPoint(vIdx)
            val halfWayPoint = partWay(distance)(p)(nextPoint)
            Some((halfWayPoint, halfWayPoint -> idx))
        }
        .zipWithIndex
        .map { case (point, i) => point.toPixel(Color.getHSBColor(0.3f, 1.0f, 1.0f)) }
        .take(50000)
        .foreach(pixel => canvas.setRGB(pixel.x, pixel.y, pixel.color.getRGB))

    override def paintComponent(g: Graphics2D): Unit = {
      plotPoints()
      g.drawImage(canvas, null, null)
    }

  }

  case class Vector(x: Double, y: Double) {
    def times(factor: Double): Vector = Vector(x * factor, y * factor)
  }
  case class Point(x: Int, y: Int) {
    def to(p2: Point): Vector = Vector(p2.x - this.x, p2.y - this.y)
    def add(v: Vector): Point = Point(this.x + v.x.toInt, this.y + v.y.toInt)
    def toPixel(c: Color): Pixel = Pixel(this, c)
  }
  case class Pixel(p: Point, color: Color) {
    val x: Int = p.x
    val y: Int = p.y
  }

  val polygon: Array[Point] = Array(
    Point(300, 10),
    Point(15, 217),
    Point(124, 553),
    Point(476, 553),
    Point(585, 217)
  )

  val randomPoint: Int => (Point, Int) =
    n => (polygon(n), Random.nextInt(polygon.length))

  val partWay: Double => Point => Point => Point =
    d => start => end => start.add(start.to(end).times(d))

  var distance: Double = 2d / 3

  override def top: MainFrame = new MainFrame {
    contents = new DataPanel(this) {
      preferredSize = new Dimension(W, H)
    }
  }
}
