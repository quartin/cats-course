package part1intro

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Essentials {
  //values
  val aBoolean: Boolean = false

  //expressions are evaluated to a value
  val anIfExpression: String = if (2 > 3) "bigger" else "smaller"

  //instructions vs expressions
  //side effect = actions that do not evaluate to a meaningful value
  // but do something (show something on screen, write to file)
  val theUnit: Unit = println("Hello, Scala") // Unit = void = {}

  //OOP
  class Animal
  class Cat extends Animal
  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  // inheritance model: extend at most 1 class but inherit from several traits
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch!")
  }

  // singleton
  object MySingleton // singleton pattern in one line

  //companions
  object Carnivore // companion object of the class Carnivore

  // generics
  class MyList[A]

  // method notation
  val three: Int = 1 + 2 //infix expression
  val anotherThree: Int = 1.+(2)

  // functional programming
  val incrementer: Int => Int = x => x + 1 // anonymous value x can infer it's of type int
  val incremented: Int = incrementer(45) // 46

  // for-comprehension
  val checkerBoard: Seq[(Int, Char)] = List(1,2,3)
    .flatMap(n => List('a', 'b', 'c').map(c => (n,c))) //hard to read

  val anotherCheckerBoard: Seq[(Int, Char)] = for { //chain of fm and map
    n <- List(1,2,3)
    c <- List('a', 'b', 'c')
  } yield (n,c) //equivalent to the one above


  // HOF map, flatMap, filter
  val processedList: Seq[Int] = List(1,2,3).map(incrementer) // list(2,3,4)
  val aLongerList: Seq[Int] = List(1,2,3).flatMap(x => List(x, x+1)) // list(1,2,2,3,3,4)

  // option(some/none) and try(success/failure)
  val anOption: Option[Int] = Option(/*something that might be null*/ 3) // Some(3)
  val doubledOption: Option[Int] = anOption.map(_ * 2) // Some(6)

  val anAttempt: Try[Int] = Try(/*something that might throw*/ 42) // Success(42)
  val aModifiedAttempt: Try[Int] = anAttempt.map(_ + 10) // Success(52)

  // pattern matching
  // allows decomposition of values
  val unknown: Any = 45
  val ordinal: String = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  val optionDescription: String = anOption match {
    case Some(value) => s"the option is not empty: $value"
    case None => "the option is empty"
  }

  // Futures
  implicit val ec: ExecutionContext = ExecutionContext
    .fromExecutorService(Executors.newFixedThreadPool(8))

  val aFuture: Future[Int] = Future{
    // a bit of code
    42
  }

  //  wait for completion (async)
  // onComplete is a partial function
  aFuture.onComplete { //evaluated asynchronously
    case Success(value) => println(s"The async meaning of life is $value")
    case Failure(exception) => println(s"Meaning of life failed $exception")
  }

  // map a Future
  val anotherFuture: Future[Int] = aFuture.map(_ + 1) // Future(43 when it completes

  // partial functions
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 43
    case 8 => 56
    case 100 => 999
  }

  // some more advance stuff
  trait HigherKindedType[F[_]]
  trait SequenceChecker[F[_]] {
    def isSequential: Boolean
  }

  // will use in cats a lot
  val listChecker: SequenceChecker[List] = new SequenceChecker[List] {
    override def isSequential: Boolean = true
  }

  def main(args: Array[String]): Unit = {
  }
}
