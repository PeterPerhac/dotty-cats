import contextual.examples.binary._
import contextual.examples.hex._
import contextual.examples.email._


object StringStuff {
  def main(args: Array[String]): Unit = {
    for {
      h <- hex"""cafebabe"""
    } print(" " + h)

    println()

    for {
      b <- bin"""01010100010101101010010010101100"""
    } print(" " + b)

    println()

    println(email"""info@scala.world""")
  }
}
