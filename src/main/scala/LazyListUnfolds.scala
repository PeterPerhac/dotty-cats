object LazyListUnfolds extends App {

  LazyList.unfold((0, 1)) { case (m, n) => Some((m, (n, m + n))) }.take(10).toList.foreach(println)
  println("---")

}
