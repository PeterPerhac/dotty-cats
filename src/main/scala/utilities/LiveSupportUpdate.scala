package utilities

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, FormatStyle}
import scala.util.{Random => r}

object LiveSupportUpdate {

  val images: List[String] = List(
    """༼ つ ◕_◕ ༽つ""",
    """(。◕‿◕｡)""",
    """༼ʘ̚ل͜ʘ̚༽""",
    """(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧""",
    """¯\(◉‿◉)/¯""",
    """(▀̿Ĺ̯▀̿ ̿)"""
  )

  private def image = images(r.nextInt(images.size))

  def main(args: Array[String]): Unit = {
    val d = LocalDate.now()
    val formattedDate = d.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    println(s"""*Live Support Update for $formattedDate*""")
    args.foreach(a => println(s""" ⭒ $a"""))
    println()
    println(s"That's all folks $image")
  }

}
