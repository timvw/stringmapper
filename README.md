# String Mapper

This library automates the task of mapping Strings to Things.
A typical use-case is parsing CSV into a case class.

[![Build Status](https://api.travis-ci.org/timvw/stringmapper.png?branch=master)](https://travis-ci.org/timvw/stringmapper)
[![Maven Central](https://img.shields.io/maven-central/v/be.icteam/stringmapper_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/be.icteam/stringmapper_2.12)

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

## Development

Compile and test:

```bash
sbt +clean; +cleanFiles; +compile; +test
```

Install a snapshot in your local maven repository:

```bash
sbt +publishM2
```

## Release

Set the following environment variables:
- PGP_PASSPHRASE
- PGP_SECRET
- SONATYPE_USERNAME
- SONATYPE_PASSWORD

Leveraging the [ci-release](https://github.com/olafurpg/sbt-ci-release) plugin:

```bash
sbt ci-release
```

Find the most recent release:

```bash
git ls-remote --tags $REPO | \
  awk -F"/" '{print $3}' | \
  grep '^v[0-9]*\.[0-9]*\.[0-9]*' | \
  grep -v {} | \
  sort --version-sort | \
  tail -n1
```

Push a new tag to trigger a release via [travis-ci](https://travis-ci.org/github/timvw/stringmapper):

```bash
v=v1.0.5
git tag -a $v  -m $v
git push origin $v
```

## License

Code is provided under the Apache 2.0 license available at http://opensource.org/licenses/Apache-2.0, as well as in the LICENSE file. This is the same license used as Spark and Frameless.

