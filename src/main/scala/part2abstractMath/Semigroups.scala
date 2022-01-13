package part2abstractMath

object Semigroups extends App {
  // Semigroups COMBINE elements of the same type

  import cats.Semigroup
  import cats.instances.int._

  val naturalIntSemigroup = Semigroup[Int]
  val intCombination = naturalIntSemigroup.combine(2, 46) // addition
  println(intCombination)


  import cats.instances.string._

  val natStringSemigroup = Semigroup[String]
  val strComb = natStringSemigroup.combine("I love ", "Cats") // concatenation
  println(strComb)

  // standard of combination between two values
  def reduceInts(list: List[Int]): Int = list.reduce(naturalIntSemigroup.combine)

  val numbers = (1 to 10).toList
  println(reduceInts(numbers))

  def reduceStrings(list: List[String]): String = list.reduce(natStringSemigroup.combine)

  val strs = List("I'm ", "starting ", "to ", "like ", "semigroups")
  println(reduceStrings(strs))

  // can we define a general API to reduce things?
  def reduceThings[T](list: List[T])(implicit semigroup: Semigroup[T]): T = list.reduce(semigroup.combine)

  println(reduceThings(numbers)) // compiler injects the implicit Semigroup[Int]
  println(reduceThings(strs)) // compiler injects the implicit Semigroup[String]

  import cats.instances.option._ // compiler will produce an implicit Semigroup[Option[Int]]

  // produces an optional with the summed elements
  val numberOptions: List[Option[Int]] = numbers.map(n => Option(n)).appended(None)
  println(reduceThings(numberOptions)) // an Optionp[Int] containing the sum of all the numbers

  val strOptions: List[Option[String]] = strs.map(s => Option(s))
  println(reduceThings(strOptions))


  // EXERCISE - SUPPORT A NEW TYPE
  // hint: use same pattern with Eq
  case class Expense(id: Long, amount: Double)
  implicit val expenseSemigroup: Semigroup[Expense] = Semigroup.instance[Expense] { (e1, e2) => {
      val maxExpenseId = Seq(e1,e2).maxBy(e => e.amount).id
      Expense(maxExpenseId, e1.amount + e2.amount)
    }
  }

  val firstExpense = Expense(id = 123L, amount = 10.0)
  val lastExpense = Expense(id = 124L, amount = 9.5)
  val expenses = List(firstExpense, lastExpense)
  println(reduceThings(expenses))

  // extension methods from semigroup - |+| (combine function)
  import cats.syntax.semigroup._
  val anIntSum = 2 |+| 3 // requires the presence of an implicit Semigroup[Int]
  val aStringConcat = "we like" |+| "semigroups"
  val aCombinedExpense = Expense(4, 80) |+| Expense(56, 46)

  println(aCombinedExpense)

  // EXERCISE - implement reduceThings2 with the |+|
  def reduceThings2[T](list: List[T])(implicit semigroup: Semigroup[T]): T = list.reduce(_ |+| _)
  println(reduceThings2(expenses))

  // can use type context
  def reduceThings3[T : Semigroup](list: List[T]): T = list.reduce(_ |+| _)
  // add implicit parameter of type Semigroup[T]
  println(reduceThings3(expenses))


  // cats TYPE CLASS Semigroup: Combine two values of the same type
  // Use cases: data structures meant to be combined
  // -- data integration & bd processing
  // eventual consistency & distributed computing
}
