package be.icteam.stringmapper

import org.scalatest.funsuite.AnyFunSuite

class StringToThingTests extends AnyFunSuite {

  test("should map to string as expected") {
    val sut = implicitly[StringToThing[String]]
    assert(sut.map("X") == Right("X"))
  }

  test("should map int as expected") {
    val sut = implicitly[StringToThing[Int]]
    assert(sut.map("3") == Right(3))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map double as expected") {
    val sut = implicitly[StringToThing[Double]]
    assert(sut.map("3.0") == Right(3.0))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }

  test("should map option as expected") {
    val sut = implicitly[StringToThing[Option[Int]]]
    assert(sut.map("3") == Right(Some(3)))
    assert(sut.map("") == Right(None))
    assert(sut.map("X") == Left(List("""java.lang.NumberFormatException: For input string: "X"""")))
  }
}
