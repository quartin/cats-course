package part1intro

object CatsIntro extends App {
  // Eq type class - type safe equality

  // this compiles :(, triggers compiler warning, always returns false
  val aComparison: Boolean = 2 == "a string"
  println(s"aComparison: $aComparison")

  // part 1 - type class import
  import cats.Eq

 // part 2 - import type class (TC) instances for the types we need
  import cats.instances.int._

  // part 3 - Use the TC API
  val intEquality: Eq[Int] = Eq[Int]
  val aTypeSafeComparison: Boolean = intEquality.eqv(2, 3)
  println(s"aTypeSafeComparison: $aTypeSafeComparison")
  // doesn't compile :) val anUnsafeComparison = intEquality.eqv(2, "a string")

  // part 4 - use extension methods (if applicable)
  import cats.syntax.eq._
  // === EqOps (we also require import cats.instances.int._)
  // extension methods are only visible in the presence of the right TC instance
  val anotherTypeSafeComp: Boolean = 2 === 3
  val neqComparison: Boolean = 2 =!= 3
  println(s"anotherTypeSafeComp: $anotherTypeSafeComp")
  println(s"neqComparison: $neqComparison")



  // part 5 - extending the TC operations to composite types, eg, lists
  // we need to import the instances.list
  import cats.instances.list._ // we bring Eq[List[Int]] in scope
  // needs Eq[Int] in scope
  val aListComparison = List(2) === List(3)
  println(s"aListComparison: $aListComparison")

  // part 6 - create a TC instance for a custom type
  case class ToyCar(model: String, price: Double)
  // construct your own TC instances...
  implicit val toyCarEq: Eq[ToyCar] = Eq.instance[ToyCar] {
    (car1, car2) => car1.price == car2.price
  }

  val compareToyCars = ToyCar("Ferrari", 29.99) === ToyCar("Ford", 29.99)
  println(s"comparing toy cars: $compareToyCars")

  // CATS organization
  //
  // Use your type class API
  // import cats.YourTypeClass
  //
  // Bring the implicit TC instances for your supported type in scope
  // import cats.instances.yourType._
  //
  // Use extension methods your TC supports
  // import cats.syntax.yourTypeClass._
  //
  // ..some imports are not self-evident -- import all
  // import cats._
  // import cats.implicits._
}
