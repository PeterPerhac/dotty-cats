case class Preference(enabled:Boolean)

case class User(name: String, preferences: Seq[Preference])


object SeqsVsLists {

  def topPreference(user:User):String =
    user.preferences match {
      case p1 :: _ => s"Head preference: $p1"
      case Nil => s"no preferences"
    }

  def main(args: Array[String]):Unit = {
    val user = User("Peter", Seq(Preference(true)))
    println("Hello "+user.name)
    println(topPreference(user))

    val lUser = User("listUser", List(Preference(true)))
    println("Hello "+lUser.name)
    println(topPreference(lUser))

    val vUser = User("vectorUser", Vector(Preference(true)))
    println("Hello "+vUser.name)
    println(topPreference(vUser))
  }

}
