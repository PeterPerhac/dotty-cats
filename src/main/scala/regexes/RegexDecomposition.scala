package regexes

import java.time.LocalDate

object RegexDecomposition {

  def main(args: Array[String]): Unit = {
    val date = """(\d\d\d\d)-(\d\d)-(\d\d)""".r

    println("2004-01-20" match {
      case date(_ *) => "It's a date!"
      case _         => "it's not a date"
    })

    println("2004-01-20" match {
      case date(_)       => "It's a date!"
      case date(y, m, d) => s"it's a date  $y/$m/$d"
    })

    println("20040120" match {
      case date(_ *) => "It's a date!"
      case _         => "it's not a date"
    })

    println("20040120" match {
      case date(_) => "It's a date!"
      case _       => "it's not a date"
    })

    Some("2017-05-14")
      .collect {
        case date(y, m, d) => LocalDate.of(y.toInt, m.toInt, d.toInt)
      }
      .foreach(println)

  }

}
