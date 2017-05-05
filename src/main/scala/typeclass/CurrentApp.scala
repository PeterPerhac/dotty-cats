package typeclass

object CurrentApp {

  import Current.syntax._

  def getTodayString()(implicit today: Current[java.time.LocalDate]): String =
    today.current().toString

  def getTodayString2: String = {
    implicit val _ = java.time.LocalDate.of(2017, 5, 1).toCurrent
    getTodayString()
  }

  def main(args: Array[String]): Unit = {
    println(getTodayString())
    println(getTodayString2)
  }

}
