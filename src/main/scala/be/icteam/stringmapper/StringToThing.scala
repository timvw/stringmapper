package be.icteam.stringmapper

import shapeless.labelled._

import scala.util._

trait StringToThing[T] {
    def map(value: String): MapResult[T]
}

object StringToThing {

    def apply[T](fn: String => T): StringToThing[T] = new StringToThing[T] {
        def map(value: String): MapResult[T] = {
            Try {
                fn(value)
            } match {
                case Success(t) => MapResult.success[T](t)
                case Failure(exception) => MapResult.failure[T](exception.toString)
            }
        }
    }

    def to[T](fn: String => T): StringToThing[T] = StringToThing[T](fn)

    implicit val stringToDouble: StringToThing[Double] = to[Double](_.toDouble)
    implicit val stringToFloat: StringToThing[Float] = to[Float](_.toFloat)
    implicit val stringToLong: StringToThing[Long] = to[Long](_.toLong)
    implicit val stringToInt: StringToThing[Int] = to[Int](_.toInt)
    implicit val stringToShort: StringToThing[Short] = to[Short](_.toShort)
    implicit val stringToByte: StringToThing[Byte] = to[Byte](_.toByte)
    implicit val stringToBoolean: StringToThing[Boolean] = to[Boolean](_.toBoolean)
    implicit val stringToString: StringToThing[String] = to[String](identity)

    implicit def stringToOption[T: StringToThing]: StringToThing[Option[T]] = new StringToThing[Option[T]] {
        def map(value: String): MapResult[Option[T]] = {
            if (value.isEmpty) {
                MapResult.success(None)
            }
            else implicitly[StringToThing[T]].map(value) match {
                case Left(msgs) => MapResult.failure(msgs: _*)
                case Right(t) => MapResult.success(Some(t))
            }
        }
    }

    implicit def stringTofieldType[K, H](implicit hParser: StringToThing[H]): StringToThing[FieldType[K, H]] = new StringToThing[FieldType[K, H]] {
        def map(line: String): MapResult[FieldType[K, H]] = {
            hParser.map(line) match {
                case Left(msgs) => MapResult.failure(msgs: _*)
                case Right(h) => MapResult.success(field[K].apply(h))
            }
        }
    }
}