package be.icteam.stringmapper

trait CsvParser[T] {
  def parse(line: String): MapResult[T]
}

object CsvParser {
  def apply[T : StringsToThing]: CsvParser[T] = {
    (line: String) => {
      val strings = line.split(",", -1)
      implicitly[StringsToThing[T]].map(strings)
    }
  }
}
