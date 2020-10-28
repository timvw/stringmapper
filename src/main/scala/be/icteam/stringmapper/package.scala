package be.icteam

import scala.util._

package object stringmapper {

  type MapResult[T] = Either[List[String], T]

  object MapResult {
    def success[T](t: T): MapResult[T] = Right(t)
    def failure[T](msgs: String*): MapResult[T] = Left(List(msgs:_*))
  }
}
