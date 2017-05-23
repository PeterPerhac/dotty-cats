package functional_reactive

sealed trait DayOfWeek {

  val value: Int

  override def toString: String = value match {
    case 1 => "Monday"
    case 2 => "Tuesday"
    case 3 => "Wednesday"
    case 4 => "Thursday"
    case 5 => "Friday"
    case 6 => "Saturday"
    case 7 => "Sunday"
  }

}

object DayOfWeek {

  def dayOfWeek(d: Int): Option[DayOfWeek] = Some(d) collect {
    case x if 1 to 7 contains x => new DayOfWeek {
      val value = x
    }
  }

}


object InRange {

  def unapply(tpl: Tuple2[Range, Int]): Option[Int] = PartialFunction.condOpt(tpl._2) {
    case no if tpl._1 contains no => no
  }

}
