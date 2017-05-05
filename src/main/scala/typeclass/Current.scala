package typeclass

trait Current[T] {
  def current(): T
}

object Current {

  def apply[T](implicit current: Current[T]) = current

  implicit object localDateCurrent extends Current[java.time.LocalDate] {
    override def current(): java.time.LocalDate = java.time.LocalDate.now()
  }

  implicit object jodaDateTimeCurrent extends Current[org.joda.time.DateTime] {
    override def current(): org.joda.time.DateTime = org.joda.time.DateTime.now()
  }

  implicit object juDateCurrent extends Current[java.util.Date] {
    override def current(): java.util.Date = new java.util.Date()
  }

  implicit object jsqlDateCurrent extends Current[java.sql.Date] {
    override def current(): java.sql.Date = new java.sql.Date(System.currentTimeMillis())
  }

  implicit object timeMillis extends Current[Long] {
    override def current(): Long = System.currentTimeMillis()
  }

  object syntax {
    implicit class CurrentOps[T](constant: T) {
      def toCurrent: Current[T] = new Current[T] {
        override def current(): T = constant
      }
    }
  }

}

