package gui

import java.awt.Color
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

import scala.swing._
import scala.swing.event.{Key, KeyPressed}
import scala.util.Random

object DrawingApp extends SimpleSwingApplication {

  val W = 1024
  val H = 1024
  val r = 500

  private var polyPointCount: Int = 5
  private var polygon: Array[Point] = newPolygon()

  val canvas = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB)

  class DataPanel(frame: MainFrame) extends Panel {

    private var doBlackout: Boolean = true
    private var distance: Double = 0.66
    private var pixelCount: Int = 20000
    private var restrictPointChoice: Boolean = false

    def updateTitle(): Unit =
      frame.title = s"$polyPointCount-sided polygon. " +
        f"distance = $distance%.2f, " +
        s"points = $pixelCount ${if (restrictPointChoice) ", restricted choice of points" else ""}"

    def doRefresh(): Unit = {
      if (doBlackout) {
        val g = canvas.createGraphics()
        g.setBackground(Color.BLACK)
        g.setColor(Color.BLACK)
        g.fill(new Rectangle2D.Float(0f, 0f, W.toFloat, H.toFloat))
        g.dispose()
      }
      plotPoints()
      updateTitle()
      this.repaint()
    }

    listenTo(keys)
    focusable = true
    requestFocusInWindow()

    reactions += {
      case KeyPressed(_, Key.Up, _, _) =>
        distance = distance + 0.005d
        doRefresh()
      case KeyPressed(_, Key.Down, _, _) =>
        distance = distance - 0.005d
        doRefresh()
      case KeyPressed(_, Key.S, _, _) =>
        pixelCount = pixelCount + 100
        doRefresh()
      case KeyPressed(_, Key.X, _, _) =>
        pixelCount = pixelCount - 100
        doRefresh()
      case KeyPressed(_, Key.Space, _, _) =>
        doBlackout = !doBlackout
        doRefresh()
      case KeyPressed(_, Key.R, _, _) =>
        restrictPointChoice = !restrictPointChoice
        doRefresh()
      case KeyPressed(_, Key.A, _, _) =>
        polyPointCount = polyPointCount + 1
        polygon = newPolygon()
        if (doBlackout) doRefresh() else updateTitle()
      case KeyPressed(_, Key.Z, _, _) =>
        if (polyPointCount > 3) {
          polyPointCount = polyPointCount - 1
          polygon = newPolygon()
          if (doBlackout) doRefresh() else updateTitle()
        }
    }

    val randomPoint: Int => (Point, Int) =
      prevIdx => {
        if (restrictPointChoice) {
          val rIdx = LazyList
            .unfold(prevIdx) { last =>
              val res = Random.nextInt(polyPointCount)
              PartialFunction.condOpt(res) { case n if last == prevIdx => n -> n }
            }
            .last
          polygon(rIdx) -> rIdx
        } else {
          (polygon(prevIdx), Random.nextInt(polygon.length))
        }
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
        .slice(1, pixelCount + 1)
        .foreach(pixel => if (pixel.withinBounds(W, H)) { canvas.setRGB(pixel.x, pixel.y, pixel.color.getRGB) })

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

    def withinBounds(maxX: Int, maxY: Int): Boolean =
      p.x >= 0 && p.x < maxX && p.y >= 0 && p.y < maxY

    val x: Int = p.x
    val y: Int = p.y
  }

  def polyPoints(sides: Int, cx: Int, cy: Int, r: Int): Array[Point] = {
    import math._
    val step: Double = 2 * Pi / sides
    (0 until sides).toArray.map { idx =>
      Point((cos(idx * step - Pi / 2) * r).round.toInt, (sin(idx * step - Pi / 2) * r).round.toInt).add(Vector(cx, cy))
    }
  }

  def newPolygon(): Array[Point] = polyPoints(polyPointCount, W / 2, H / 2, 5 * W / 12)

  val partWay: Double => Point => Point => Point =
    d => start => end => start.add(start.to(end).times(d))

  override def top: MainFrame = new MainFrame {
    contents = new DataPanel(this) {
      preferredSize = new Dimension(W, H)
      updateTitle()
    }
  }
}
