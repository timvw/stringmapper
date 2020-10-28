package be.icteam.stringmapper

import shapeless._
import shapeless.labelled._
import org.scalatest.funsuite.AnyFunSuite

class CsvParserTests extends AnyFunSuite {

    case class Item(title: String, name: String, id: Int, last: Option[String])

    val conformInput = "sir,mike,3,x"
    val malformedLeadingSpaceInId = "sir,mike, 3,x"
    val malformedMissingColumn = "sir,mike,3"
    val malformedAdditionalColumn = "sir,mike,3,x,BAD"

    test("it should be possible to parse csv to a case-class") {
        val itemCsvParser = CsvParser[Item]
        assert(itemCsvParser.parse(conformInput) == Right(Item("sir", "mike", 3, Some("x"))))
        assert(itemCsvParser.parse(malformedLeadingSpaceInId) == Left(List("java.lang.NumberFormatException: For input string: \" 3\"")))
        assert(itemCsvParser.parse(malformedMissingColumn) == Left(List("unexpected end of line, still need to parse columns..")))
        assert(itemCsvParser.parse(malformedAdditionalColumn) == Left(List("expected end of line, but still have 'BAD'")))
    }

    test("it should be possible to use a custom parser") {

        val itemParser: StringsToThing[Item] = {

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

        val customItemCsvParser = CsvParser(itemParser)
        assert(customItemCsvParser.parse(malformedLeadingSpaceInId) == Right(Item("sir", "MIKE", 3, Some("x"))))

        implicit val x = itemParser
        val custom2ItemCsvParser = CsvParser[Item]
        assert(custom2ItemCsvParser.parse(malformedLeadingSpaceInId) == Right(Item("sir", "MIKE", 3, Some("x"))))
    }
}