package part2abstractMath

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
import scala.concurrent.{ExecutionContext, Future}

object Monads extends App {
  // list
  val numbersList: List[Int] = List(1, 2, 3)
  val charsList: List[Char] = List('a', 'b', 'c')
  // Exercise 1.1 - how would you create all the combinations of number & character
  // IDENTICAL implementations
  val numberCharsList = for {
    n <- numbersList
    c <- charsList
  } yield (n, c)
  val numberCharsListAlternative = numbersList.flatMap(n => charsList.map(c => (n, c)))

  println(numberCharsList)
  println(numberCharsListAlternative)

  // options
  val numberOption = Option(2)
  val charOption = Option('d')
  // Exercise 1.2 : how would you create the combination of (number, character)
  println(numberOption.flatMap(n => charOption.map(c => (n, c))))
  println(for {
    n <- numberOption
    c <- charOption
  } yield (n, c))

  // futures
  val executor: ExecutorService = Executors.newFixedThreadPool(8)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
  val numberFuture = Future(42)
  val charFuture = Future('Z')
  // Exercise 1.3 : how would you create the combination of (number, character)
  val comb1 = numberFuture.flatMap(n => charFuture.map(c => (n, c)))
  comb1.onComplete(println)

  /*
    Pattern
    - wrapping a value into a M (monadic) value
    - the flatMap mechanism - general transformation pattern, not just sequential data structures

   MONADS - Higher Kinded TC
   */
  trait MyMonad[M[_]] {
    def pure[A](value: A): M[A]

    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

    def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(a => (f andThen pure).apply(a))
  }

  // Cats Monad

  import cats.Monad
  import cats.instances.option._

  val optionMonad = Monad[Option]
  val anOption = optionMonad.pure(4) // Option(4) == Some(4)
  println(anOption)
  val aTransformedOption = optionMonad.flatMap(anOption)(x => if (x % 3 == 0) Some(x + 1) else None)
  println(aTransformedOption)

  import cats.instances.list._

  val listMonad = Monad[List]
  val aList = listMonad.pure(3) // List(3)
  val aTransformedList = listMonad.flatMap(aList)(x => List(x, x + 1))
  println(aTransformedList)

  // Exercise: use a Monad[Future]

  import cats.instances.future._

  val futureMonad = Monad[Future]
  val aFuture = futureMonad.pure(4) //Future(4)
  val aTransformedFuture = futureMonad.flatMap(aFuture)(x => Future(x * 10))
  aTransformedFuture.onComplete(println)

  // useful for general APIs
  def getPairsList(numbers: List[Int], chars: List[Char]): List[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))

  def getPairsOption(numbers: Option[Int], chars: Option[Char]): Option[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))

  def getPairsFuture(numbers: Future[Int], chars: Future[Char]): Future[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))

  // instead of duplicating...
  def getPairs[M[_], A, B](ma: M[A], mb: M[B])(implicit monad: Monad[M]): M[(A, B)] = {
    monad.flatMap(ma)(a => monad.map(mb)(b => (a, b)))
  }

  println(getPairs(numbersList, charsList))
  println(getPairs(numberOption, charOption))
  getPairs(numberFuture, charFuture) onComplete println

  // extension methods - weirder imports (pure & flatMap)

  import cats.syntax.applicative._ // pure is here

  val oneOption = 1.pure[Option] // implicit Monad[Option] will be used = Some(1)
  val oneList = 1.pure[List] // implicit Monad[List] that's in scope

  import cats.syntax.flatMap._ // flatMap is here

  val oneOptionTranformed = oneOption.flatMap(x => (x + 1).pure[Option])

  // EXERCISE:
  // implement def map[A, B](ma: M[A])(f: A => B): M[B] (MyMonad)
  // in terms of flatMap & pure

  // We can say a Monad extends a Functor, because the monad can provide the map
  // MONADS EXTEND FUNCTORS

  def getPairsShorter[M[_] : Monad, A, B](ma: M[A], mb: M[B]): M[(A, B)] = {
    ma.flatMap(a => mb.flatMap(b => (a, b).pure[M]))
  }

  // can actually be

  import cats.syntax.functor._ // map IS here

  def getPairsEvenShorter[M[_] : Monad, A, B](ma: M[A], mb: M[B]): M[(A, B)] = {
    ma.flatMap(a => mb.map(b => (a, b)))
  }

  println(getPairsEvenShorter(numbersList, charsList))
  println(getPairsEvenShorter(numberOption, charOption))
  getPairsEvenShorter(numberFuture, charFuture) onComplete println

  val oneOptionMapped = Monad[Option].map(Option(2))(_ + 1)

  // We have access to for-comprehensions
  val composedOptionFor = for {
    one <- 1.pure[Option]
    two <- 2.pure[Option]
  } yield one + two
  println(composedOptionFor)

  // EXERCISE: getPairs w/ for-comprehension
  def getPairsFor[M[_] : Monad, A, B](ma: M[A], mb: M[B]): M[(A, B)] = for {
    a <- ma
    b <- mb
  } yield (a, b)

  println(getPairsFor(numbersList, charsList))
  println(getPairsFor(numberOption, charOption))
  getPairsFor(numberFuture, charFuture) onComplete println

  // Monad Higher-kinded TC that provides:
  // - a pure method to wrap a normal value into a monadic value
  // - a flatMap method to transform monadic values in chain
  // pure in applicative._ TC
  // flatmap in flatmap._ TC
  // map in functor._ TC

  // Monad can implement map in terms of pure + flatmap,
  // so monads extend functors

  // map + flatmap = for-comprehensions

  // Use cases: sequential transformations
  // - list combinations
  // - option transformations
  // - asynchronous chained computations
  // - dependent computations
  // - sequential computations
  // for comprehensions are NOT ITERATION
  // flatMap is a mental model of CHAINED transformations

  executor.awaitTermination(2, TimeUnit.SECONDS)
  executor.shutdown()
}
