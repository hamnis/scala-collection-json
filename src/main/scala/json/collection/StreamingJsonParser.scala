package json.collection

import java.io.Reader
import jsonij.json.Constants._
import collection.mutable.ListBuffer
import java.net.URI
import jsonij.json._
import java.lang.IllegalStateException

class StreamingJsonParser {
  private val parser = new JSONParser

  def parse(reader: Reader): Either[Exception, JsonCollection] = {
    val target = new ReaderJSONReader(reader)
    val read = target.read()
    if (read == -1) {
      Left(new JSONParserException("invalidEmpty"))
    }
    else if (read != OPEN_OBJECT) {
      Left(new JSONParserException("invalidObjectExpecting1", target.getLineNumber, target.getPositionNumber, Array(OPEN_OBJECT.asInstanceOf[Char], read.asInstanceOf[Char])))
    }
    try {
      val field = extractField(target)
      if (!"collection".equals(field)) {
        Left(new IllegalArgumentException("Wrong field; expected 'collection' got '%s'".format(field)))
      }
      else {
        readObject(target, parseCollection) match {
          case Some(x) => Right(x)
          case None => Left(new IllegalArgumentException("Failed to parse collection"))
        }
      }
    }
    catch {
      case e: Exception => Left(e)
    }
  }

  private val parseCollection: (JSONReader) => JsonCollection = {
    target => {
      var version: Option[Version] = None
      var href: Option[URI] = None
      var items = List[Item]()
      var queries = List[Query]()
      var template: Option[Template] = None
      var error: Option[ErrorMessage] = None
      var links = List[Link]()
      while (target.peek() != CLOSE_OBJECT) {
        if (target.peek() == VALUE_SEPARATOR) {
          target.read()
        }
        val field = extractField(target).trim()

        field match {
          case "version" => version = Some(Version(extractString(target)))
          case "href" => href = Some(URI.create(extractString(target)))
          case "items" => items = readObjectList(target, parseItem)
          case "queries" => queries = readObjectList(target, parseQuery)
          case "links" => links = readObjectList(target, parseLink)
          case "template" => template = readObject(target, parseTemplate)
          case "error" => error = readObject(target, parseError)
        }
      }

      JsonCollection(version.getOrElse(Version.ONE), href.getOrElse(fail("Collection")), links, items, queries, template, error)
    }
  }


  private val parseItem: (JSONReader) => Item = {
    target => {
      var properties = List[Property]()
      var links = List[Link]()
      var href: Option[URI] = None
      populateFields(target) {
        case "href" => href = Some(URI.create(extractString(target)))
        case "data" => properties = parseData(target)
        case "links" => links = readObjectList(target, parseLink)
      }
      Item(href.getOrElse(fail("Item")), properties, links)
    }
  }

  def fail(value: String): Nothing = {
    throw new IllegalStateException("Failed to parse %s, missing href".format(value))
  }

  private val parseQuery: (JSONReader) => Query = {
    target => {
      var properties = List[Property]()
      var href: Option[URI] = None
      var prompt: Option[String] = None
      var rel: Option[String] = None
      populateFields(target) {
        case "href" => href = Some(URI.create(extractString(target)))
        case "data" => properties = parseData(target)
        case "rel" => rel = Some(extractString(target))
        case "prompt" => prompt = Some(extractString(target))
      }
      Query(href.getOrElse(fail("Query")), rel.getOrElse(fail("Query")), prompt, properties)
    }
  }


  private val parseLink: (JSONReader) => Link = {
    target => {
      var prompt: Option[String] = None
      var rel: Option[String] = None
      var href: Option[URI] = None
      var render: Option[Render] = None

      populateFields(target){
        case "href" => href = Some(URI.create(extractString(target)))
        case "rel" => rel = Some(extractString(target))
        case "prompt" => prompt = Some(extractString(target))
        case "render" => render = Some(Render(extractString(target)))
      }

      Link(href.getOrElse(fail("Link")), rel.getOrElse(fail("Link")), prompt, render.getOrElse(Render.LINK))
    }
  }

  private val parseTemplate: (JSONReader) => Template = {
    target => {
      if ("data" == extractField(target)) {
        Template(parseData(target))
      }
      else {
        throw new IllegalArgumentException("Illegal property found")
      }
    }
  }

  private val parseError: (JSONReader) => ErrorMessage = {
    target => {
      var title: Option[String] = None
      var code: Option[String] = None
      var message: Option[String] = None
      populateFields(target) {
        case "title" => title = Some(extractString(target))
        case "code" => code = Some(extractString(target))
        case "message" => message = Some(extractString(target))
      }
      ErrorMessage(title.getOrElse(fail("ErrorMessage")), code, message)
    }

  }

  private def parseData(target: JSONReader): List[Property] = readObjectList(target, parseProperty)

  private val parseProperty: (JSONReader) => Property = {
    target => {
      var name: Option[String] = None
      var value: Option[Value[_]] = None
      var prompt: Option[String] = None
      populateFields(target) {
        case "name" => name = Some(extractString(target))
        case "prompt" => prompt = Some(extractString(target))
        case "value" => value = Some(parseValue(target))
      }
      val actualName = name.getOrElse(throw new IllegalStateException("Name missing from property"))
      value.map(PropertyWithValue(actualName, prompt, _)).getOrElse(PropertyWithoutValue(actualName, prompt))
    }
  }

  private def readObject[A](target: JSONReader, f: (JSONReader) => A): Option[A] = {
    if (target.peek() != OPEN_OBJECT) {
      None
    }
    else {
      var obj: Option[A] = None
      while (target.read() == OPEN_OBJECT) {
        obj = Some(f(target))
      }
      obj
    }
  }

  def populateFields(target: JSONReader)(f: PartialFunction[String, Unit]) {
    while (target.peek() != CLOSE_OBJECT) {
      if (target.peek() == VALUE_SEPARATOR) {
        target.read()
      }
      val field = extractField(target)
      if (f.isDefinedAt(field)) {
        f(field)
      }
    }
  }

  private def readObjectList[A](target: JSONReader, f: (JSONReader) => A): List[A] = {
    if (target.peek() != OPEN_ARRAY) {
      Nil
    }
    else {
      val buffer = ListBuffer[A]()
      while (target.read() != CLOSE_ARRAY) {
        readObject(target, f) foreach (buffer += _)
      }
      buffer.toList
    }
  }


  private def extractField(target: JSONReader): String = {
    val field = extractString(target)
    if (target.read() != NAME_SEPARATOR) {
      throw new IllegalArgumentException("Expected a property separator")
    }
    field
  }

  private final def parseValue(target: JSONReader) = {
    val value = target.peek match {
      case QUOTATION => extractString(target)
      case x if ConstantUtility.isNumeric(x) => parser.parseNumeric(target).getNumber
      case x if TRUE_STR.charAt(0) == x => true
      case x if FALSE_STR.charAt(0) == x => false
      case x if NULL_STR.charAt(0) == x => null
    }
    Value(value)
  }

  private def extractString(target: JSONReader): String = {
    parser.parseString(target).toString
  }
}
