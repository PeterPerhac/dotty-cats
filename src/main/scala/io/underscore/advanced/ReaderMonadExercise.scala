package io.underscore.advanced

import cats.data.Reader

case class Db(usernames: Map[Int, String], passwords: Map[String, String])

object ReaderMonadExercise {

  type DbReader[A] = cats.data.Reader[Db, A]

  val db = Db(
    Map(1      -> "dade", 2          -> "kate", 3           -> "margo"),
    Map("dade" -> "zerocool", "kate" -> "acidburn", "margo" -> "secret")
  )

  def main(args: Array[String]): Unit = {

    //to enable false.pure
    import cats.syntax.applicative._

    def findUsername(userId: Int): DbReader[Option[String]] =
      Reader(db => db.usernames.get(userId))

    def checkPassword(username: String, password: String): DbReader[Boolean] =
      Reader(db => db.passwords.exists { case (u, p) => u == username && p == password })

    def checkLogin(userId: Int, password: String): DbReader[Boolean] =
      for {
        un            <- findUsername(userId)
        authenticated <- un.map(checkPassword(_, password)).getOrElse(false.pure[DbReader])
        //      authenticated <- checkPassword(un.getOrElse(""), password)
      } yield authenticated

    println(checkLogin(1, "zerocool").run(db))
    println(checkLogin(4, "davinci").run(db))
    println(checkLogin(2, "aacidburn").run(db))
    println(checkLogin(2, "acidburn").run(db))

  }

}
