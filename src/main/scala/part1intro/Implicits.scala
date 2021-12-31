package part1intro

object Implicits {
  // implicit classes
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name"
  }

  // single arg
  implicit class ImpersonateString(name: String) {
    def greet: String = Person(name).greet
  }

  // explicit
  val impersonableString = new ImpersonateString("Peter")
  impersonableString.greet

  // implicit conversion
  val greeting: String = "Peter".greet // implicitly does enw ImpersonateString("Peter")

  // importing implicit conversions in scope

  import scala.concurrent.duration._

  // without import below does not compile
  val oneSec: FiniteDuration = 1.second

  // implicit arguments and values
  def increment(x: Int)(implicit amount: Int): Int = x + amount

  implicit val defaultAmount: Int = 10
  val incremented2: Int = increment(2) // implicit argument 10 is passed by the compiler

  def multiply(x: Int)(implicit times: Int): Int = x * times

  val times2: Int = multiply(2)

  // more complex example
  trait JSONSerializer[T] {
    def toJson(value: T): String
  }

  def listToJson[T](list: List[T])(implicit serializer: JSONSerializer[T]): String = {
    list.map(v => serializer.toJson(v)).mkString("[", ",", "]")
  }

  implicit val personSerializer: JSONSerializer[Person] = (person: Person) => {
    s"""
       |{"name": "${person.name}"}
       |""".stripMargin
  }
  val personJson: String = listToJson(List(Person("Alice"), Person("Bob")))
  //implicit argument is used to PROVE THE EXISTENCE of a type

  //implicit method
  implicit def oneArgCaseClassSerializer[T <: Product]: JSONSerializer[T] = new JSONSerializer[T] {
    override def toJson(value: T): String = {
      s"""
         |{"${value.productElementName(0)}": "${value.productElement(0)}"}
         |""".stripMargin.trim
    }
  }

  case class Cat(name: String)
  val catsToJson: String = listToJson(List(Cat("Tom"), Cat("Garfield")))
  // in the background, compiler: listToJson(List(Cat("Tom"), Cat("Garfield")))(oneArgCaseClassSerializer[Cat])
  // implicit methods are used to PROVE THE EXISTENCE of a type
  // can be used for implicit conversions (DISCOURAGED - use implicit classes )

  def main(args: Array[String]): Unit = {
    val cat: Cat = Cat("whiskers")
    val person: Person = Person("David")

    println(oneArgCaseClassSerializer[Cat].toJson(cat))
    println(oneArgCaseClassSerializer[Person].toJson(person))
    val catsToJson: String = listToJson(List(Cat("Tom"), Cat("Garfield")))

    println(catsToJson)
  }
}
