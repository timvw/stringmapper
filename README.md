# String Mapper

This library automates the task of mapping Strings to Things.
A typical use-case is parsing CSV into a case class.

## Usage

```scala
import be.icteam.stringmapper._

case class Item(title: String, name: String, id: Int, last: Option[String])

val conformInput = "sir,mike,3,x"
val malformedLeadingSpaceInId = "sir,mike, 3,x"
val malformedMissingColumn = "sir,mike,3"
val malformedAdditionalColumn = "sir,mike,3,x,BAD"

val itemCsvParser = CsvParser[Item]
assert(itemCsvParser.parse(conformInput) == Right(Item("sir", "mike", 3, Some("x"))))
assert(itemCsvParser.parse(malformedLeadingSpaceInId) == Left(List("java.lang.NumberFormatException: For input string: \" 3\"")))
assert(itemCsvParser.parse(malformedMissingColumn) == Left(List("unexpected end of line, still need to parse columns..")))
assert(itemCsvParser.parse(malformedAdditionalColumn) == Left(List("expected end of line, but still have 'BAD'")))
```

Customizing StringToThing:

```scala

val stringToItem: StringsToThing[Item] = {

    implicit def customIntFieldParser[K <: Symbol]: StringToThing[FieldType[K, Int]] = {
        StringToThing.stringTofieldType[K, Int](StringToThing[Int](_.trim.toInt))
    }

    implicit def customStringFieldParser[K <: Symbol](implicit witness: Witness.Aux[K]): StringToThing[FieldType[K, String]] = {
        val nameParser = StringToThing[String](_.toUpperCase())
        val hParserToUse =
            if(witness.value == Symbol("name")) nameParser
            else implicitly[StringToThing[String]]
        StringToThing.stringTofieldType[K, String](hParserToUse)
    }

    implicitly[StringsToThing[Item]]
}

implicit val customStringToItem = stringToItem
val customItemCsvParser = CsvParser[Item]
assert(customItemCsvParser.parse(malformedLeadingSpaceInId) == Right(Item("sir", "MIKE", 3, Some("x"))))
```

