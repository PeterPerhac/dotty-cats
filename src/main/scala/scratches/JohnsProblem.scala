package scratches

object JohnsProblem {

  type ComparingFunction[A] = (A, A) => Int

  case class Comparison[A, B](a1: A, a2: A)(f: ComparingFunction[B], bf: A => B) extends Function0[Int] {
    def apply: Int = f(bf(a1), bf(a2))
  }

  case class Cat(name: String, age: Int)

  val intComparison: ComparingFunction[Int] = // _ - _
    (i1: Int, i2: Int) => {
      val res = i1 - i2
      println(res)
      res
    }

  val stringComparison: ComparingFunction[String] = // _ compareTo _
    (s1: String, s2: String) => {
      val res = s1 compareTo s2
      println(res)
      res
    }

  def main(args: Array[String]): Unit = {
    val comparisons = Seq(
      Comparison(Cat("mitzix", 10), Cat("foobar", 10))(intComparison, _.age),
      Comparison(Cat("abcdex", 20), Cat("abcdex", 50))(stringComparison, _.name),
      Comparison(Cat("zimitx", 30), Cat("arfoob", 30))(intComparison, _.age),
      Comparison(Cat("tzimix", 40), Cat("barfoo", 50))(intComparison, _.age),
      Comparison(Cat("xmitzi", 50), Cat("barfoo", 40))(intComparison, _.age),
      Comparison(Cat("ixmitz", 40), Cat("barfoo", 30))(intComparison, _.age)
    )

    val nonZero: Int =
      comparisons.foldLeft(0)((res, compare) => if (res != 0) res else compare())

    println("---")
    println(nonZero)
  }

}
