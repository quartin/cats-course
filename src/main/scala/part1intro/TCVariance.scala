package part1intro

object TCVariance extends App {
  import cats.Eq
  import cats.instances.int._ // brings into scope Eq[Int] instance
  import cats.instances.option._ // construct a Eq[Option[Int]] TC instance
  import cats.syntax.eq._

  val aComparison = Option(2) === Option(3)
//  val anInvalidComparison = Some(2) === None // Eq[Some[Int]] not found! it is not a subtype

  // VARIANCE
  class Animal
  class Cat extends Animal

  // covariant type: subtyping is propagated to the generic type
  class Cage[+T]
  val cage: Cage[Animal] = new Cage[Cat] // Cat <: Animal, so Cage[Cat] <: Cage[Animal]

  // contravariant type: subtyping is propagated BACKWARDS to the generic type
  class Vet[-T]
  val vet: Vet[Cat] = new Vet[Animal] // Cat <: Animal, then Vet[Animal] <: Vet[Cat]
  // an animal vet works on any animal, so works for your cat as well

  // rule of thumb: "HAS a T" = covariant, "ACTS/OPERATES on T" = contravariant
  // variance affects how TC instances are being fetched

  // contravariant: "act on T" type class
  trait SoundMaker[-T]
  implicit object AnimalSoundMaker extends SoundMaker[Animal]

  def makeSound[T](implicit soundMaker: SoundMaker[T]): Unit = println("wow")

  makeSound[Animal] // ok -- AnimalSoundMaker TC instance defined above
  makeSound[Cat] // ok -- TC instance for animal is also applicable to Cats
  // rule 1: contravariant TCs can use the superclass instances if nothing
  // is available strictly for that type

  implicit object OptionSoundMaker extends SoundMaker[Option[Int]]
  makeSound[Option[Int]]
  makeSound[Some[Int]]

  // EQ is invariant so, has implications for subtypes

  // covariant: "has a T" type class
  // part1
  trait AnimalShow[+T] {
    def show: String
  }

  // part2
  implicit object GeneralAnimalShow extends AnimalShow[Animal] {
    override def show: String = "animals everywhere"
  }
  implicit object CatsShow extends AnimalShow[Cat] {
    override def show: String = "so many cats!"
  }

  // part 3
  def organizeShow[T](implicit event: AnimalShow[T]): String = event.show
  // rule 2: covariant TCs will always use the more TC instance for that type
  // but may confuse the compiler if the general TC is also present

  println(s"organize cat show ${organizeShow[Cat]}") // ok - compiler will inject CatsShow as implicit
//  organizeShow[Animal] will not compile -- ambiguous values

  // rule 3: you can't have both benefits
  // CATS has decided to use INVARIANT TCs
  val validComparison = Option(2) === Option.empty[Int]
  // use general type instead of some/none

    println(s"a valid comparison: $validComparison")

}
