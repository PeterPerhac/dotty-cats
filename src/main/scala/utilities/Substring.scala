package utilities

object Substring {
  def main(args: Array[String]): Unit = {

    val fn = "PL124-foobar-lol.jpg"

    val treated = fn.substring(fn.indexOf('-') + 1)

    println(treated)

  }

}
