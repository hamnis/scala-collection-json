package json.collection

import java.io.Reader
import jsonij.json.Constants._
import jsonij.json.{JSONReader, JSONParserException, JSONParser, ReaderJSONReader}

class JsonParser {
  private val parser = new JSONParser
  def parse(reader: Reader): Either[Exception, JsonCollection] = {
    val r = new ReaderJSONReader(reader)
    val peek = r.peek()
    if (peek == -1) {
      Left(new JSONParserException("invalidEmpty"))
    }
    else if (peek != OPEN_OBJECT) {
      Left(new JSONParserException("invalidObjectExpecting1", r.getLineNumber, r.getPositionNumber, Array(OPEN_OBJECT.asInstanceOf[Char], peek.asInstanceOf[Char])))
    }
    try {
      r.read()
      val field = parser.parseString(r).toString
      if (!"collection".equals(field)) {
        Left(new IllegalArgumentException("Wrong field; expected 'collection' got '%s'".format(field)))
      }
      else {
        r.read()
        Right(parseCollection(r))
      }

    }
    catch {
      case e: Exception => Left(e)
    }
  }

  private def parseCollection(target: JSONReader) = {
    var version: Option[Version] = None
    var items = List[Item]()
    var queries = List[Query]()
    var template: Option[Template] = None
    var error: Option[ErrorMessage] = None
    var links = List[Link]()
    while (target.read() == CLOSE_OBJECT) {
      val field = parser.parseString(target).toString
      if (target.read() != NAME_SEPARATOR) {
        throw new IllegalArgumentException("Expected a property separator")
      }
      field match {
        case "version" => version = Some(Version(parser.parseString(target).toString))
        case "items" => items = parseItems(target)
        case "queries" => queries = parseQueries(target)
        case "links" => links = parseLinks(target)
        case "template" => template = parseTemplate(target)
        case "error" => error = parseError(target)
      }
    }
    JsonCollection(version.getOrElse(Version.ONE), links, items, error, template, queries)
  }
  private def parseItems(target: JSONReader): List[Item] = {
    List()
  }

  private def parseQueries(target: JSONReader): List[Query] = {
    List()
  }

  private def parseLinks(target: JSONReader): List[Link] = {
    List()
  }

  private def parseTemplate(target: JSONReader): Option[Template] = {
    None
  }

  private def parseError(target: JSONReader): Option[ErrorMessage] = {
    None
  }
}
