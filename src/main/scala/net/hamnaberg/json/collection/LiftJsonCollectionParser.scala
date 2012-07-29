package net.hamnaberg
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

object LiftJsonCollectionParser extends JsonCollectionParser {
  private val EMPTY_ARRAY = Some(JArray(Nil))
  private val EMPTY_VALUE = Some(JNothing)

  def parseCollection(reader: Reader): Either[Exception, JsonCollection] = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parsed match {
        case JObject(List(JField("collection", x: JObject))) => parseCollection(x).map(Right(_)).getOrElse(Left(new Exception("Failed to parse collection...")))
        case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(parsed))
      }
    }
    catch {
      case e : Exception => Left(e)
    }
  }


  def parseTemplate(reader: Reader) = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parsed match {
        case JObject(List(JField("collection", x: JObject))) => toTemplate(x).map(Right(_)).getOrElse(Left(new Exception("Failed to parse Template...")))
        case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(parsed))
      }
    }
    catch {
      case e : Exception => Left(e)
    }
  }

  private def fieldAsMap(fields: List[JField]): Map[String, JsonAST.JValue] = {
    fields.foldLeft(Map[String, JValue]())((coll, b) => coll + (b.name -> b.value))
  }

  private val toLink: PartialFunction[JValue, List[Link]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      val toRender: PartialFunction[JValue, Render] = {
        case JString(x) => Render(x)
      }
      for {
        AsURI(href) <- map.get("href")
        AsString(rel) <- map.get("rel")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        render = map.get("render").map(toRender)
      } yield Link(href, rel, prompt, render)
    }.toList
  }


  private val toItem: PartialFunction[JValue, List[Item]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsURI(href) <- map.get("href")
        AsList(data) <- map.get("data").orElse(EMPTY_ARRAY)
        AsList(links) <- map.get("links").orElse(EMPTY_ARRAY)
      } yield Item(href, toData(data), toLinks(links))
    }.toList
  }

  private val toQuery: PartialFunction[JValue, List[Query]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      for {
        AsURI(href) <- map.get("href")
        AsString(rel) <- map.get("rel")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        AsList(data) <- map.get("data").orElse(EMPTY_ARRAY)
      } yield Query(href, rel, prompt, toData(data))
    }.toList
  }

  private val toProperty: PartialFunction[JValue, List[Property]] = {
    case JObject(fields) => {
      val map = fieldAsMap(fields)
      val property = for {
        AsString(name) <- map.get("name")
        AsOptionalString(prompt) <- map.get("prompt").orElse(EMPTY_VALUE)
        value = map.get("value").flatMap{
          case JNothing => None
          case x => Value(x)
        }
        array = map.get("array").map {
          case JNothing => Nil
          case JArray(x) => x.flatMap(Value(_).toList)
          case _ => sys.error("Unexpected value type here")
        }.getOrElse(Nil)
        obj = map.get("object").map {
          case JNothing => Map.empty[String, Value[_]]
          case JObject(f) => {
            val m: List[(String, Option[Value[_]])] = f.map {
              case JField(n, v) => n -> Value(v)
            }
            m.collect{case (k, Some(v)) => k -> v}.toMap[String, Value[_]]
          }
          case _ => sys.error("Unexpected value type here")
        }.getOrElse(Map.empty[String, Value[_]])
      } yield {
        if (!obj.isEmpty) ObjectProperty(name, prompt, obj)
        else if (!array.isEmpty) ListProperty(name, prompt, array)
        else ValueProperty(name, prompt, value)
      }
      property.toList
    }
  }

  private def toLinks(list: List[JValue]) : List[Link] = list.flatMap(toLink)
  private def toItems(list: List[JValue]) : List[Item] = list.flatMap(toItem)
  private def toData(list: List[JValue]): List[Property] = list.flatMap(toProperty)
  private def toQueries(list: List[JValue]): List[Query] = list.flatMap(toQuery)
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
      } yield ErrorMessage(title, code, message)
    }
  }

  private def parseCollection(obj: JObject): Option[JsonCollection] = {
    val fields = fieldAsMap(obj.obj)
    for {
      AsURI(href) <- fields.get("href")
      AsString(version) <- fields.get("version").orElse(Some(JString("1.0")))
      AsList(links) <- fields.get("links").orElse(EMPTY_ARRAY)
      AsList(items) <- fields.get("items").orElse(EMPTY_ARRAY)
      AsList(queries) <- fields.get("queries").orElse(Some(JArray(Nil)))
      AsObject(template) <- fields.get("template").orElse(Some(JObject(Nil)))
      AsObject(error) <- fields.get("error").orElse(Some(JObject(Nil)))
    } yield JsonCollection(Version(version), href, toLinks(links), toItems(items), toQueries(queries), toTemplate(template), toError(error))
  }


  private object AsList {
    def unapply(value: JArray) = Some(value.arr)
  }

  private object AsString {
    def unapply(str: JString) = Some(str.values)
  }

  private object AsOptionalString {
    def unapply(string: JValue) = string match {
      case JString(x) => Some(Some(x))
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
