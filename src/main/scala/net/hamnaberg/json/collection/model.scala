package net.hamnaberg
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

  def addLink(link: Link) = copy(error = None, links = link :: links)

  def addQuery(query: Query) = copy(error = None, queries = query :: queries)

  def withTemplate(template: Template) = copy(error = None, template = Some(template))

  def withError(error: ErrorMessage) = JsonCollection(version, href, links, Nil, Nil, None, Some(error))

  def isError = error.isDefined

  def toJson: JValue = {
    val items = {
      val i = this.items.map(_.toJson)
      if (i.isEmpty) JNothing else JArray(i)
    }
    val links = {
      val l = this.links.map(_.toJson)
      if (l.isEmpty) JNothing else JArray(l)
    }
    val queries = {
      val q = this.queries.map(_.toJson)
      if (q.isEmpty) JNothing else JArray(q)
    }
    if (isError) {
      ("collection" ->
        ("version" -> version.name) ~
          ("error" -> error.map(_.toJson))
        )
    }
    else {
      ("collection" ->
        ("version" -> version.name) ~
          ("href" -> href.toString) ~
          ("links" -> links) ~
          ("items" -> items) ~
          ("queries" -> queries) ~
          ("template" -> template.map(_.toJson))
        )
    }
  }
}

trait ToJson {
  def toJson: JValue
}

object JsonCollection {

  def apply(href: URI): JsonCollection = JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, None)

  def apply(href: URI, error: ErrorMessage): JsonCollection =
    JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, Some(error))

  def apply(href: URI,
            links: List[Link],
            items: List[Item],
            queries: List[Query],
            template: Option[Template]
             ): JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, template, None)

  def apply(href: URI,
            links: List[Link],
            items: List[Item],
            queries: List[Query]): JsonCollection =
    JsonCollection(Version.ONE, href, links, items, queries, None, None)

  def apply(href: URI, links: List[Link], item: Item): JsonCollection =
    JsonCollection(Version.ONE, href, links, List(item), Nil, None, None)

  def apply(item: Item): JsonCollection =
    JsonCollection(Version.ONE, item.href, Nil, List(item), Nil, None, None)

  def apply(href: URI,
            links: List[Link],
            items: List[Item]): JsonCollection =
    JsonCollection(Version.ONE, href, links, items, Nil, None, None)

}

sealed trait Version {
  def name: String
}

object Version {
  def apply(id: String): Version = id match {
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

case class ErrorMessage(title: String, code: Option[String], message: Option[String]) extends ToJson {
  def toJson = {
    ("title" -> title) ~
      ("code" -> code) ~
      ("message" -> message)
  }
}

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

/*
case class StringListValue(list: List[String]) extends Value {
  def value = list

  def toJson = JArray(list.map(JString(_)))

  type A = List[String]
}

case class NumericListValue(list: List[BigDecimal]) extends Value {
  def value = list

  def toJson = JArray(list.map(v => if (v.isValidInt) JInt(v.intValue()) else JDouble(v.doubleValue())))

  type A = List[BigDecimal]
}*/

case class Link(href: URI, rel: String, prompt: Option[String] = None, render: Render = Render.LINK) {
  def toJson: JValue = {
    ("href" -> href.toString) ~
      ("rel" -> rel) ~
      ("prompt", prompt) ~
      ("render", render.name)
  }
}

case class Item(href: URI, data: List[Property], links: List[Link]) extends ToJson with PropertyContainer {
  def toJson: JValue = {
    val data = {
      val list = this.data.map(_.toJson)
      if (list.isEmpty) JNothing else JArray(list)
    }
    val links = {
      val list = this.links.map(_.toJson)
      if (list.isEmpty) JNothing else JArray(list)
    }
    ("href" -> href.toString) ~
    ("data" -> data) ~
    ("links" -> links)
  }


  type T = Item

  protected def copyData(data: List[Property]) = copy(data = data)

  def toTemplate = Template(data)
}

case class Query(href: URI, rel: String, prompt: Option[String], data: List[Property]) extends ToJson with PropertyContainer{

  type T = Query

  protected def copyData(data: List[Property]) = copy(data = data)

  def toJson: JValue = {
    ("href" -> href.toString) ~
      ("rel" -> rel) ~
      ("prompt" -> prompt) ~
      ("data" -> data.map(_.toJson))
  }

  def toURI: URI = {
    val query = data.map(p => (p.name, p.value.map(_.value.toString).getOrElse(""))).mkString("", "&", "")
    new URI(href.getScheme, href.getAuthority, href.getHost, href.getPort, href.getPath, query, href.getFragment)
  }
}

case class Template(data: List[Property]) extends ToJson with PropertyContainer{

  type T = Template

  def toJson: JValue = {
    ("data" -> data.map(_.toJson))
  }

  protected def copyData(data: List[Property]) = copy(data)
}

private[collection] sealed trait PropertyContainer {
  type T <: PropertyContainer

  def data: List[Property]

  def getProperty(name: String) = data.find(_.name == name)

  def getPropertyValue(name: String) = data.find(_.name == name).flatMap(_.value)

  def addProperty(property: Property) = {
    val index = data.indexWhere(_.name == property.name)
    if (index == -1) {
      copyData(data ::: List(property))
    }
    else {
      copyData(data.updated(index, property))
    }
  }

  def removeProperty(name: String) = copyData(data.filterNot(_.name == name))

  protected def copyData(data: List[Property]) : T
}
