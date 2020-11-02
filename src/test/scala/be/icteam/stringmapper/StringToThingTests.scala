package be.icteam.stringmapper

import org.scalatest.funsuite.AnyFunSuite

class StringToThingTests extends AnyFunSuite {

  test("should map double as expected") {
    val sut = implicitly[StringToThing[Double]]
    assert(sut.map("3.0") == Right(3.0))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map float as expected") {
    val sut = implicitly[StringToThing[Float]]
    assert(sut.map("3.0") == Right(3.0f))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map long as expected") {
    val sut = implicitly[StringToThing[Long]]
    assert(sut.map("4") == Right(4L))
  }

  test("should map int as expected") {
    val sut = implicitly[StringToThing[Int]]
    assert(sut.map("3") == Right(3))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map short as expected") {
    val sut = implicitly[StringToThing[Short]]
    assert(sut.map("3") == Right(3))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map byte as expected") {
    val sut = implicitly[StringToThing[Byte]]
    assert(sut.map("1") == Right(1))
    assert(sut.map("256") == Left(List("""java.lang.NumberFormatException: Value out of range. Value:"256" Radix:10""")))
  }

  test("should map boolean as expected") {
    val sut = implicitly[StringToThing[Boolean]]
    assert(sut.map("true") == Right(true))
    assert(sut.map("false") == Right(false))
    assert(sut.map("256") == Left(List("""java.lang.IllegalArgumentException: For input string: "256"""")))
  }

  test("should map to string as expected") {
    val sut = implicitly[StringToThing[String]]
    assert(sut.map("X") == Right("X"))
  }

  test("should map option as expected") {
    val sut = implicitly[StringToThing[Option[Int]]]
    assert(sut.map("3") == Right(Some(3)))
    assert(sut.map("") == Right(None))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }
}
