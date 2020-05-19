package gui

import java.awt.Color
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

import scala.swing._
import scala.swing.event.{Key, KeyPressed}
import scala.util.Random

case class Vector(x: Double, y: Double) {
  def *(factor: Double): Vector = Vector(x * factor, y * factor)
}
case class Point(x: Int, y: Int) {
  def to(p2: Point): Vector = Vector(p2.x - this.x, p2.y - this.y)
  def +(v: Vector): Point = Point(this.x + v.x.toInt, this.y + v.y.toInt)
  def toPixel(c: Color): Pixel = Pixel(this, c)
}
case class Pixel(p: Point, color: Color) {

  def withinBounds(maxX: Int, maxY: Int): Boolean =
    p.x >= 0 && p.x < maxX && p.y >= 0 && p.y < maxY

  val x: Int = p.x
  val y: Int = p.y
}

object Fractals extends SimpleSwingApplication {

  private val A = 1024
  private val R = A / 4

  class DataPanel(frame: MainFrame) extends Panel {

    val canvas = new BufferedImage(A, A, BufferedImage.TYPE_INT_RGB)

    private var polyPointCount: Int = 3
    private var doBlackout: Boolean = true
    private var distance: Double = 0.50
    private var pixelCount: Int = 50000
    private var restrictPointChoice: Boolean = false

    private var polygon: Array[Point] = newPolygon(polyPointCount)

    def newPolygon(nSides: Int): Array[Point] = {
      import math._
      def int(d: Double): Int = d.round.toInt
      val step: Double = 2 * Pi / nSides
      val halfPi = Pi / 2
      (0 until nSides).toArray.map { idx =>
        Point(int(cos(idx * step - halfPi) * R), int(sin(idx * step - halfPi) * R)) + Vector(A / 2, A / 2)
      }
    }

    val partWay: Double => Point => Point => Point =
      d => start => end => start + (start.to(end) * d)

    def updateTitle(): Unit =
      frame.title = s"$polyPointCount-sided polygon. " +
        f"distance = $distance%.2f, " +
        s"points = $pixelCount ${if (restrictPointChoice) ", restricted choice of points" else ""}"

    def doRefresh(): Unit = {
      if (doBlackout) {
        val g = canvas.createGraphics()
        g.setBackground(Color.BLACK)
        g.setColor(Color.BLACK)
        g.fill(new Rectangle2D.Float(0f, 0f, A.toFloat, A.toFloat))
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
        if (doBlackout) doRefresh() else updateTitle()
      case KeyPressed(_, Key.R, _, _) =>
        restrictPointChoice = !restrictPointChoice
        doRefresh()
      case KeyPressed(_, Key.A, _, _) =>
        polyPointCount = polyPointCount + 1
        polygon = newPolygon(polyPointCount)
        if (doBlackout) doRefresh() else updateTitle()
      case KeyPressed(_, Key.Z, _, _) =>
        if (polyPointCount > 3) {
          polyPointCount = polyPointCount - 1
          polygon = newPolygon(polyPointCount)
          if (doBlackout) doRefresh() else updateTitle()
        }
      case KeyPressed(_, Key.Q, _, _) =>
        System.exit(0)
    }

    val randomPoint: Int => (Point, Int) =
      prevIdx => {
        def randomVertexIndex(): Int = Random.nextInt(polygon.length)
        if (restrictPointChoice) {
          var newIdx = randomVertexIndex()
          while (newIdx == prevIdx) {
            newIdx = randomVertexIndex()
          }
          polygon(newIdx) -> newIdx
        } else {
          (polygon(prevIdx), randomVertexIndex())
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
        .map(_.toPixel(Color.getHSBColor(0.3f, 1.0f, 1.0f)))
        .slice(1, pixelCount + 1) //discard annoying first pixel
        .foreach(pixel => if (pixel.withinBounds(A, A)) { canvas.setRGB(pixel.x, pixel.y, pixel.color.getRGB) })

    override def paintComponent(g: Graphics2D): Unit = {
      plotPoints()
      g.drawImage(canvas, null, null)
    }

  }

  override def top: MainFrame = new MainFrame {
    contents = new DataPanel(this) {
      preferredSize = new Dimension(A, A)
      updateTitle()
    }
  }
}
