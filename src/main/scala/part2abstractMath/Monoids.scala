package part2abstractMath

object Monoids extends App {
  import cats.Semigroup
  import cats.instances.int._
  import cats.syntax.semigroup._ // import the |+|
  val numbers = (1 to 1000).toList
  // |+| is always associative
  val sumLeft = numbers.foldLeft(0)(_ |+| _)
  val sumRight = numbers.foldRight(0)(_ |+| _)
  println(sumLeft)
  println(sumRight)

  // define a general API
//  def combineFold[T : Semigroup](list: List[T]): T = list.foldLeft(/**WHAT**/)(_ |+| _)

  // MONOID
  import cats.Monoid
  val intMonoid = Monoid[Int]
  val combineInt = intMonoid.combine(23, 999)
  println(combineInt)
  val zeroInt = intMonoid.empty // provide neutral element
  println(zeroInt)

  import cats.instances.string._ // bring the implicit Monoid[String] in scope
  val emptyString = Monoid[String].empty
  println(emptyString)
  val combineString = Monoid[String].combine("I understand ", "a monoid")
  println(combineString)

  import cats.instances.option._ // construct an implicit Monoid[Option[Int]]
  val emptyOption = Monoid[Option[Int]].empty // None
  val combineOption = Monoid[Option[Int]].combine(Option(2), Option.empty[Int])
  println(combineOption) // Some(2)

  // extension methods for Monoids - |+| (combine) monoids extend semigroups
//  import cats.syntax.monoid._ // either this one or cats.syntax.semigroup._ (not both at same time)
  val combineOptionFancy = Option(3) |+| Option(7)
  println(combineOptionFancy)

  // EXERCISE: Implement a combineFold
  def combineFold[T : Monoid](list: List[T]): T = list.foldLeft(Monoid[T].empty)(_ |+| _)
  val listInts = List(13, 4, 3)
  val listStr = List("hey ", "ho ", "let's go")
  println(combineFold(listInts))
  println(combineFold(listStr))

  // EXERCISE: combine a list of phonebooks as Maps[String, Int]
  // hint: don't construct a monoid - use an import
  val phonebooks = List(
    Map(
      "Alice" -> 234,
      "Bob" -> 647,
    ),
    Map(
      "Charlie" -> 372,
      "Daniel" -> 889
    ),
    Map(
      "Tina" -> 123
    )
  )
  println(phonebooks.flatten.toMap) //derp
  // the exercise
  import cats.instances.map._
  println(combineFold(phonebooks))

  // EXERCISE: Shopping cart and online stores w/ Monoids
  case class ShoppingCart(items: List[String], total: Double)
  implicit val monoidShoppingCart: Monoid[ShoppingCart] = Monoid.instance[ShoppingCart](
    ShoppingCart(List.empty, 0.0),
    (sc1, sc2) => ShoppingCart(sc1.items ++ sc2.items, sc1.total + sc2.total))

  def checkout(shoppingCarts: List[ShoppingCart]): ShoppingCart = combineFold(shoppingCarts)
  val shoppingCarts = List(
    ShoppingCart(items = List("bag", "cleaner"), total = 2.89),
    ShoppingCart(items = List("salad", "broccoli", "spinach", "clementines"), total = 4.20),
    ShoppingCart(items = List("flour", "sugar", "chocolate"), total = 4.99),
    ShoppingCart(items = List(), total = 0.0),
  )
  println(checkout(shoppingCarts))

  // Monoids are a natural extension of semigroups that include a neutral value.
  // This is a value `e` in A, that for every a in A, a |+| e = a
}
