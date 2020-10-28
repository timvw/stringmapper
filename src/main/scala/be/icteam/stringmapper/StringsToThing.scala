package be.icteam.stringmapper

import shapeless._
import shapeless.labelled._

trait StringsToThing[T] {
    def map(line: Array[String]): MapResult[T]
}

object StringsToThing {

    implicit val stringsToHNil: StringsToThing[HNil] = new StringsToThing[HNil] {
        def map(line: Array[String]): MapResult[HNil] = {
            if(line.isEmpty) MapResult.success(HNil)
            else MapResult.failure(s"expected end of line, but still have '${line.mkString(",")}'")
        }
    }

    implicit def stringsToHList[K <: Symbol, H, T <: HList](implicit witness: Witness.Aux[K], hParser: Lazy[StringToThing[FieldType[K, H]]], tLineParser: StringsToThing[T]): StringsToThing[FieldType[K, H] :: T] = new StringsToThing[FieldType[K, H] :: T] {
        def map(line: Array[String]) = {
            if (line.isEmpty) MapResult.failure(s"unexpected end of line, still need to parse columns..")
            else {
                (hParser.value.map(line.head), implicitly[StringsToThing[T]].map(line.tail)) match {
                    case (Right(h), Right(t)) => MapResult.success(h :: t)
                    case (Right(_), Left(tmsgs)) => MapResult.failure(tmsgs: _*)
                    case (Left(hmsgs), Right(_)) => MapResult.failure(hmsgs: _*)
                    case (Left(hmsgs), Left(tmsgs)) => MapResult.failure(hmsgs ++ tmsgs: _*)
                }
            }
        }
    }

    implicit def parser[T, R](implicit gen: LabelledGeneric.Aux[T, R], rparser: StringsToThing[R]): StringsToThing[T] = new StringsToThing[T] {
        def map(line: Array[String]) = {
            rparser.map(line) match {
                case Left(msgs) => MapResult.failure(msgs: _*)
                case Right(r) => MapResult.success(gen.from(r))
            }
        }
    }
}