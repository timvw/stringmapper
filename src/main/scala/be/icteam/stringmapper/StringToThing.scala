package be.icteam.stringmapper

import shapeless.labelled._

import scala.util._

trait StringToThing[T] {
    def map(value: String): MapResult[T]
}

object StringToThing {

    def apply[T](fn: String => T): StringToThing[T] = new StringToThing[T] {
        def map(value: String) = {
            Try {
                fn(value)
            } match {
                case Success(t) => MapResult.success[T](t)
                case Failure(exception) => MapResult.failure[T](exception.toString)
            }
        }
    }

    implicit val stringToString: StringToThing[String] = StringToThing[String](identity)
    implicit val stringToInt: StringToThing[Int] = StringToThing[Int](_.toInt)
    implicit val stringToDouble: StringToThing[Double] = StringToThing[Double](_.toDouble)

    implicit def stringToOption[T: StringToThing]: StringToThing[Option[T]] = new StringToThing[Option[T]] {
        def map(value: String) = {
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
        def map(line: String) = {
            hParser.map(line) match {
                case Left(msgs) => MapResult.failure(msgs: _*)
                case Right(h) => MapResult.success(field[K].apply(h))
            }
        }
    }
}