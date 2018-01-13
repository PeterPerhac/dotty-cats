package jozefove

import cats.Functor


object FunctionFunctor {

  type ARR[A, R] = Function1[Function1[A, R], R]

  implicit def thingFunctor[T]: Functor[({type ARRT[C] = ARR[C, T]})#ARRT] =
    new Functor[({type ARRT[C] = ARR[C, T]})#ARRT] {
      override def map[X, Y](fa: ARR[X, T])(f: X => Y): ARR[Y, T] = ??? //xtt => ytt

    }

  def main(args: Array[String]): Unit = {

    def extractAndDouble(extractor: String => Int): Int =
      ???


    //same as above
    val doublingExtractor: (String => Int) => Int =
      extractor => ???
  }

}
