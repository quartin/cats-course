package part1intro

object TypeClasses extends App {
  case class Person(name: String, age: Int)
  // part1 - type class definition
  trait JSONSerializer[T] {
    def toJSON(value: T): String
  }

  // part2 - create implicit type class INSTANCES
  implicit object StringSerializer extends JSONSerializer[String] {
    override def toJSON(value: String): String = s"$value"
  }

  implicit object IntSerializer extends JSONSerializer[Int] {
    override def toJSON(value: Int): String = value.toString
  }

  implicit object PersonSerializer extends JSONSerializer[Person] {
    override def toJSON(value: Person): String =
      s"""
         |{"name": "${value.name}", "age": "${value.age}"}
         |""".stripMargin.trim
  }

  // part3 - offer some API to serialize things to json
  def convertToJSON[T](value: T)(implicit serializer: JSONSerializer[T]): String = {
    serializer.toJSON(value)
  }

  def convertListToJSON[T](list: List[T])(implicit serializer: JSONSerializer[T]): String = {
    list.map(v => convertToJSON[T](v)).mkString("[", ",", "]")
  }


  // part4 - extending the existing types via extension methods
  object JSONSyntax {
    implicit class JSONSerializable[T](value: T)(implicit serializer: JSONSerializer[T]) {
      def toJSON: String = serializer.toJSON(value)
    }
    implicit class JSONListSerializable[T](list: List[T])(implicit serializer: JSONSerializer[T]) {
      def toJSON: String = list.map(v => serializer.toJSON(v)).mkString("[", ",", "]")
    }
  }

  // test things
  val people = List(Person("Alice", 23), Person("Xavier", 45))
  println(convertListToJSON(people))

  val bob = Person("Bob", 35)
  println(convertToJSON(bob))

  println("using extension methods")
  import JSONSyntax._
  println(people.toJSON)
  println(bob.toJSON)
}
