package json.collection

import java.io.Reader
import net.liftweb.json.JsonAST._
import java.net.URI
import net.liftweb.json.{JsonAST, JsonParser}

/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/23/11
 *
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */

class LiftJsonParser {
  private val EMPTY_ARRAY = Some(JArray(Nil))
  private val EMPTY_VALUE = Some(JString("NO_VALUE_IS_HERE"))

  def parse(reader: Reader): Either[Exception, JsonCollection] = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parse(parsed).map(Right(_)).getOrElse(Left(new Exception("Failed to parse...")))
    }
    catch {
      case e : Exception => Left(e)
    }
  }

  private def parse(value: JValue) = {
    value match {
      case JObject(List(JField("collection", x: JObject))) => parseCollection(x)
      case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(value))
    }
  }

  private def fieldAsMap(fields: List[JField]): Map[String, JsonAST.JValue] = {
    fields.foldLeft(Map[String, JValue]())((coll, b) => coll + (b.name -> b.value))
  }

  private val toLink: PartialFunction[JValue, Link] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      val toRender: PartialFunction[JValue, Render] = {
        case JString(x) => Render(x)
      }
      for {
        AsURI(href) <- map.get("href")
        AsString(rel) <- map.get("rel")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        render <- map.get("render").map(toRender).orElse(Some(Render.LINK))
      } yield Link(href, rel, prompt.value, render)
    }.get
  }


  private val toItem: PartialFunction[JValue, Item] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsURI(href) <- map.get("href")
        AsList(data) <- map.get("data").orElse(EMPTY_ARRAY)
        AsList(links) <- map.get("links").orElse(EMPTY_ARRAY)
      } yield Item(href, toData(data), toLinks(links))
    }.get
  }

  private val toQuery: PartialFunction[JValue, Query] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsURI(href) <- map.get("href")
        AsString(rel) <- map.get("rel")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        AsList(data) <- map.get("data").orElse(EMPTY_ARRAY)
        AsList(links) <- map.get("links").orElse(EMPTY_ARRAY)
      } yield Query(href, rel, prompt.value, toData(data))
    }.get
  }

  private val toProperty: PartialFunction[JValue, Property] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      val property = for {
        AsString(name) <- map.get("name")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        AsOptionValue(value) <- map.get("value").orElse(EMPTY_VALUE)
      } yield Property(name, prompt.value, value)
      property.get
    }
  }

  private def toLinks(list: List[JValue]) : List[Link] = list.map(toLink)
  private def toItems(list: List[JValue]) : List[Item] = list.map(toItem)
  private def toData(list: List[JValue]): List[Property] = list.map(toProperty)
  private def toQueries(list: List[JValue]): List[Query] = list.map(toQuery)
  private def toTemplate(obj: Option[JValue]): Option[Template] = obj.flatMap(toTemplate)
  private def toError(obj: Option[JValue]): Option[ErrorMessage] = obj.flatMap(toError)

  val toTemplate: PartialFunction[JValue, Option[Template]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsList(data) <- map.get("data").orElse(EMPTY_ARRAY)
      } yield Template(toData(data))
    }
  }

  val toError: PartialFunction[JValue, Option[ErrorMessage]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsString(title) <- map.get("title")
        AsOptionalString(code) <- map.get("code").orElse(EMPTY_VALUE)
        AsOptionalString(message) <- map.get("message").orElse(EMPTY_VALUE)
      } yield ErrorMessage(title, code.value, message.value)
    }
  }

  private def parseCollection(obj: JObject): Option[JsonCollection] = {
    val fields = fieldAsMap(obj.obj)
    val option = for {
      AsURI(href) <- fields.get("href")
      AsString(version) <- fields.get("version").orElse(Some(JString("1.0")))
      AsList(links) <- fields.get("links").orElse(EMPTY_ARRAY)
      AsList(items) <- fields.get("items").orElse(EMPTY_ARRAY)
      AsList(queries) <- fields.get("queries").orElse(Some(JArray(Nil)))
      AsObject(template) <- fields.get("template").orElse(Some(JObject(Nil)))
      AsObject(error) <- fields.get("error").orElse(Some(JObject(Nil)))
    } yield JsonCollection(Version(version), href, toLinks(links), toItems(items), toQueries(queries), toTemplate(template), toError(error))
    option
  }


  private object AsList {
    def unapply(value: JArray) = Some(value.arr)
  }

  private object AsString {
    def unapply(str: JString) = Some(str.values)
  }

  private case class OptionalString(value: Option[String])

  private object AsOptionalString {
    def unapply(string: JString) : Option[OptionalString] = {
      if ("NO_VALUE_IS_HERE" == string.values) Some(OptionalString(None))
      else Some(OptionalString(Some(string.values)))
    }
  }

  private object AsOptionValue {
    def unapply(value: JValue) : Option[Option[Value[_]]] = value match {
      case JInt(x) => Some(Some(Value(x)))
      case JString(x) => if ("NO_VALUE_IS_HERE" == x) None else Some(Some(Value(x)))
      case JDouble(x) => Some(Some(Value(x)))
      case JBool(x) => Some(Some(Value(x)))
      case JNull => Some(Some(NullValue))
      case JNothing => Some(None)
      case _ => None
    }
  }

  private object AsURI {
    def unapply(str: JString) = Some(URI.create(str.values))
  }

  private object AsObject {
    def unapply(obj: JObject) = if (obj.obj.isEmpty) Some(None) else Some(Some(obj))
  }
}
