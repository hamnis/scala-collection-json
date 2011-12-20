package json.collection

import java.net.URI
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

case class JsonCollection(version: Version = Version.ONE,
                          href: URI,
                          links: List[Link],
                          items: List[Item],
                          queries: List[Query],
                          template: Option[Template],
                          error: Option[ErrorMessage]
                          ) extends ToJson {
  def addItem(item: Item) = copy(error = None, items = item :: items)

  def addLink(link: Link) = copy(error = None, links =  link :: links)

  def addQuery(query: Query) = copy(error = None, queries =  query :: queries)
  
  def withTemplate(template: Template) = copy(error = None, template = Some(template))

  def withError(error: ErrorMessage) = JsonCollection(version, href, links, Nil, Nil, None, Some(error))
  
  def isError = error.isDefined


  def toJson: JValue = {
    ("collection" ->
      ("version" -> version.name) ~
      ("href" -> href.toString) ~
      ("links" -> links.map(_.toJson)) ~
      ("items" -> items.map(_.toJson)) ~
      ("queries" -> queries.map(_.toJson)) ~
      ("template" -> template.map(_.toJson))
    )
  }
}

trait ToJson {
  def toJson: JValue
}

object JsonCollection {

  def apply(href: URI): JsonCollection = JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, None)

  def apply(href: URI, error: ErrorMessage):JsonCollection =
    JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, Some(error))

  def apply(href: URI,
            links: List[Link],
            items: List[Item],
            queries: List[Query],
            template: Option[Template]
            ):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, template, None)

  def apply(href: URI,
            links: List[Link],
            items: List[Item],
            queries: List[Query]):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, None, None)

  def apply(href: URI, links: List[Link], item: Item): JsonCollection =
    JsonCollection(Version.ONE, href, links, List(item), Nil, None, None)

  def apply(href: URI,
            links: List[Link],
            items: List[Item]):JsonCollection =
    JsonCollection(Version.ONE, href, links, items, Nil, None, None)

}

sealed trait Version {
  def name: String
}

object Version {
  def apply(id: String) : Version = id match {
    case ONE.name => ONE
    case _ => ONE
  }

  case object ONE extends Version {
    val name = "1.0"
  }
  
  def unapply(version: Version) = Some(version.name)
}

case class Property(name: String, prompt: Option[String] = None, value: Option[Value] = None) extends ToJson {
  def toJson = {
    ("name" -> name) ~
    ("value" -> value.map(_.toJson)) ~
    ("prompt" -> prompt)
  }
}

case class ErrorMessage(title: String, code: Option[String], message: Option[String])

sealed abstract class Render(val name: String)

object Render {
  case object IMAGE extends Render("image")
  case object LINK extends Render("link")

  def apply(value: String): Render = value match {
    case "image" => IMAGE
    case "link" => LINK
    case _ => LINK
  }
}

sealed trait Value extends ToJson {
  type A
  def value: A
}

object Value {
  def apply(any: Any): Value = any match {
    case x: String => StringValue(x)
    case x: Boolean => BooleanValue(x)
    case x: Double => NumericValue(x)
    case x: Int => NumericValue(x)
    case x: Long => NumericValue(x)
    case x: BigDecimal => NumericValue(x)
    case null => NullValue
  }
}

case class StringValue(value: String) extends Value {
  def toJson = value

  type A = String
}

case class NumericValue(value: BigDecimal) extends Value {
  type A = BigDecimal

  def toJson = if (value.isValidInt) value.intValue() else value.doubleValue()
}

case class BooleanValue(value: Boolean) extends Value {
  def toJson = value

  type A = Boolean
}

case object NullValue extends Value {
  def value = null

  def toJson = JNull

  type A = Null
}

case class Link(href: URI, rel: String, prompt: Option[String] = None, render: Render = Render.LINK) {
  def toJson: JValue = {
    ("href" -> href.toString) ~
    ("rel" -> rel) ~
    ("prompt", prompt) ~
    ("render", render.name)
   }
}
case class Item(href: URI, data: List[Property], links: List[Link]) {
  def toJson: JValue = {
    ("href" -> href.toString) ~
    ("data" -> data.map(_.toJson))
    ("links" -> links.map(_.toJson))
  }
}
case class Query(href: URI, rel: String, prompt: Option[String], data: List[Property]) {
  def toJson: JValue = {
    ("href" -> href.toString) ~
    ("rel" -> rel) ~
    ("prompt" -> prompt) ~
    ("data" -> data.map(_.toJson))
  }
}
case class Template(data: List[Property]) {
  def toJson: JValue = {
    ("data" -> data.map(_.toJson))
  }
}
