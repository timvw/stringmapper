package be.icteam.stringmapper

trait CsvParser[T] {
  def parse(line: String): MapResult[T]
}

object CsvParser {
  def apply[T : StringsToThing]: CsvParser[T] = new CsvParser[T] {
    def parse(line: String): MapResult[T] = {
      val strings = line.split(",", -1)
      implicitly[StringsToThing[T]].map(strings)
    }
  }
}
