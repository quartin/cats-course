package part2abstractMath

import scala.util.Try

object Functors extends App {
  val aModifiedList = List(1, 2, 3).map(_ + 1) // List(2,3,4)
  val aModifiedOption = Option(2).map(_ + 1) // Some(3)
  val aModifiedTry = Try(42).map(_ + 1) // Success(43)

  // simplified definition
  trait MyFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: A => B): F[B] // F can be List, Option, Try...
  }

  import cats.Functor
  import cats.instances.list._ // includes Functor[List]

  val listFunctor = Functor[List]
  val incrementedNumbers = listFunctor.map(List(1, 2, 3))(_ + 1)

  import cats.instances.option._ // includes Functor[Option]

  val optionFunctor = Functor[Option]
  val incrementedOption = optionFunctor.map(Option(2))(_ + 1) // Some(3)

  import cats.instances.try_._

  val anIncrementedTry = Functor[Try].map(Try(42))(_ + 1) // Success(43)

  // important when we want to generalise a transformation
  // generalising an API
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)

  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)

  def do10xAttempt(attempt: Try[Int]): Try[Int] = attempt.map(_ * 10)

  def do10x[F[_] : Functor](container: F[Int]): F[Int] = Functor[F].map(container)(_ * 10)

  println(do10x(List(1, 2, 3)))
  println(do10x(Option(2)))
  println(do10x(Try(45)))

  // EXERCISE: define functor for a binary tree
  // hint: define an object which extends Functor[Tree] (impl map function)
  trait Tree[+T]

  case class Leaf[+T](value: T) extends Tree[T]

  case class Branch[+T](value: T, left: Tree[T], right: Tree[T]) extends Tree[T]

  implicit object FunctorTree extends Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Leaf(value) => Leaf(f(value))
      case Branch(value, left, right) => Branch(f(value), map(left)(f), map(right)(f))
    }
  }

  println(do10x[Tree](Branch(45, Leaf(10), Leaf(20))))
  // explicit type argument needed
  // compiler cannot find a Functor[Branch]
  // cats tc are invariant, cannot find an implicit functor[branch]
  // let's create smart constructors

  object Tree {
    def leaf[T](v: T): Tree[T] = Leaf(v)
    def branch[T](v: T, l: Tree[T], r: Tree[T]): Tree[T] = Branch(v, l, r)
  }

  // use these constructors instead of case class constructors

  println(do10x(Tree.branch(v = 45, l = Tree.leaf(10), r = Tree.leaf(20))))
  // we now no longer need the explicit type argument in do10x because both leaf & branch return Tree

  // extension method - map
  import cats.syntax.functor._
  val tree: Tree[Int] = Tree.branch(40, Tree.branch(5, Tree.leaf(10), Tree.leaf(30)), Tree.leaf(20))
  val incrementedTree = tree.map(_ + 1)

  // EXERCISE: shorter version of do10x method using extension methods
  def do10xShorter[F[_] : Functor](container: F[Int]): F[Int] = container.map(_ * 10)

  do10xShorter(tree)
  do10xShorter(List(10,20,30))

  // Functor HK type class that provides a map method for any data structure
  // useful for general APIs


}
