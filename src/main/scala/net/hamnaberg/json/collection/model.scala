package net.hamnaberg
package json.collection

import java.net.URI
import org.json4s._
import org.json4s.JsonDSL._
import Json4sHelpers._

case class JsonCollection private[collection](underlying: JObject) extends Extensible[JsonCollection] with ToJson {

  val version: Version = getAsString(underlying, "version").map(Version(_)).getOrElse(Version.ONE)
  val href: URI = getAsString(underlying, "href").map(URI.create(_)).getOrElse(throw new IllegalStateException("Not a valid collection+json object"))
  val links: List[Link] = getAsObjectList(underlying, "links").map(Link(_))
  val items: List[Item] = getAsObjectList(underlying, "items").map(Item(_))
  val queries: List[Query] = getAsObjectList(underlying, "queries").map(Query(_))
  val template: Option[Template] = getAsObject(underlying, "template").map(Template(_))
  val error: Option[Error] = getAsObject(underlying, "error").map(Error(_))

  //TODO: Expensive, do rather a patch of the JArray itself.
  def addItem(item: Item) = copy(replace(underlying, "items", JArray((items ++ List(item)).map(_.underlying))))

  //TODO: Expensive, do rather a patch of the JArray itself.
  def addLink(link: Link) = copy(replace(underlying, "links", JArray((links ++ List(link)).map(_.underlying))))

  //TODO: Expensive, do rather a patch of the JArray itself.
  def addQuery(query: Query) = copy(replace(underlying, "queries", JArray((queries ++ List(query)).map(_.underlying))))

  def withTemplate(template: Template) = copy(replace(underlying, "template", template.underlying))

  def copy(obj: JObject) = JsonCollection(obj)

  def withError(error: Error) = copy(replace(underlying, "error", error.underlying))

  def isError = error.isDefined

  def map[B](f: (Item) => B): List[B] = items.map(f)

  def filter(f: (Item) => Boolean): List[Item] = items.filter(f)

  def find(f: (Item) => Boolean): Option[Item] = items.find(f)

  def isItemCollection = {
    items match {
      case List(i) => i.href == href
      case _ => false
    }
  }

  def findLinkByRel(rel: String) = links.find(_.rel == rel)

  def findQueryByRel(rel: String) = queries.find(_.rel == rel)

  def images = links.filter(_.render == Some(Render.IMAGE))

  def toJson = JObject(List(JField("collection", underlying)))
}

trait ToJson {
  def toJson: JValue
}

object JsonCollection {

  def apply(version: Version, href: URI, links: List[Link], items: List[Item], queries: List[Query], template: Option[Template] = None, error: Option[Error] = None): JsonCollection = {
    val it = {
      val i = items.map(_.underlying)
      if (i.isEmpty) JNothing else JArray(i)
    }
    val li = {
      val l = links.map(_.underlying)
      if (l.isEmpty) JNothing else JArray(l)
    }
    val qu = {
      val q = queries.map(_.underlying)
      if (q.isEmpty) JNothing else JArray(q)
    }
    if (error.isDefined) {
      apply(filtered(
        ("version" -> version.name) ~
          ("href" -> href.toString) ~
          ("error" -> error.map(_.underlying)))
      )
    }
    else {
      apply(
        filtered(("version" -> version.name) ~
          ("href" -> href.toString) ~
          ("links" -> li) ~
          ("items" -> it) ~
          ("queries" -> qu) ~
          ("template" -> template.map(_.underlying)))
      )
    }
  }

  def apply(href: URI): JsonCollection = JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, None)

  def apply(href: URI, error: Error): JsonCollection =
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


sealed trait Value[A] extends ToJson {
  def value: A

  def toJson: JValue = {
    Value.toJson(this)
  }
}

object Value {
  case class StringValue(value: String) extends Value[String]
  case class NumberValue(value: BigDecimal) extends Value[BigDecimal]
  case class BooleanValue(value: Boolean) extends Value[Boolean]
  case object NullValue extends Value[Null] {
    def value = null
  }

  def apply(v: JValue): Option[Value[_]] = {
    v match {
      case JString(s) => Some(StringValue(s))
      case JDouble(d) => Some(NumberValue(BigDecimal(d)))
      case JInt(d) => Some(NumberValue(BigDecimal(d)))
      case JBool(d) => Some(BooleanValue(d))
      case JNull => Some(NullValue)
      case _ => throw new IllegalArgumentException("Illegal value type")
    }
  }

  private def toJson(value: Value[_]): JValue = value match {
    case StringValue(s) => JString(s)
    case NumberValue(n) => if (n.isValidInt) JInt(n.intValue()) else JDouble(n.doubleValue())
    case BooleanValue(n) => JBool(n)
    case NullValue => JNull
    case _ => throw new IllegalArgumentException("Unknown value type")
  }

}

sealed trait Property extends Extensible[Property] {
  type A
  lazy val name: String = getAsString(underlying, "name").getOrElse(throw new IllegalStateException("Not a valid property"))
  lazy val prompt: Option[String] = getAsString(underlying, "prompt")
  def value: A
}

object Property {
  def apply(obj: JObject): Property = {
    val map = obj.values
    obj match {
      case o if (map.contains("value")) => ValueProperty(o)
      case o if (map.contains("array")) => ListProperty(o)
      case o if (map.contains("object")) => ObjectProperty(o)
      case _ => throw new IllegalArgumentException("Uknown property type")
    }
  }
}

case class ValueProperty private[collection](underlying: JObject) extends Property {
  type A = Option[Value[_]]

  def copy(obj: JObject) = ValueProperty(obj)

  val value = (underlying \ "value").toOption.flatMap(Value(_))
}

object ValueProperty {
  def apply(name: String, prompt: Option[String] = None, value: Option[Value[_]] = None): ValueProperty = {
    apply(
      filtered(("name" -> name) ~
        ("prompt" -> prompt) ~
        ("value" -> value.map(_.toJson)))
    )
  }
}

case class ListProperty private[collection](underlying: JObject) extends Property {
  type A = Seq[Value[_]]

  def copy(obj: JObject) = ListProperty(obj)

  lazy val value = getAsValueList(underlying, "array").flatMap(Value(_))
}

object ListProperty {
  def apply(name: String, prompt: Option[String] = None, value: Seq[Value[_]] = Nil): ListProperty = {
    apply(
      filtered(("name" -> name) ~
        ("prompt" -> prompt) ~
        ("array" -> value.map(_.toJson)))
    )
  }
}

case class ObjectProperty private[collection](underlying: JObject) extends Property {
  type A = Map[String, Value[_]]

  def copy(obj: JObject) = ObjectProperty(obj)

  lazy val value = getAsObject(underlying, "object").map(obj => {
    val values : Seq[(String, Option[Value[_]])] = obj.obj.map{case (name,value) => name -> Value(value)}
    values.collect{case (a, Some(b)) => a -> b}.toMap[String, Value[_]]
  }).getOrElse(Map.empty)
}

object ObjectProperty {
  def apply(name: String, prompt: Option[String] = None, value: Map[String, Value[_]] = Map.empty): ObjectProperty = {
    apply(
      filtered(("name" -> name) ~
        ("prompt" -> prompt) ~
        ("object" -> JObject(value.map{case (x,y) => JField(x, y.toJson)}.toList)))
    )
  }
}

case class Error(underlying: JObject) extends Extensible[Error] with ToJson {

  def copy(obj: JObject) = Error(obj)

  lazy val title: String = getAsString(underlying, "title").getOrElse(throw new IllegalStateException("Expected title, was nothing"))
  lazy val code: Option[String] = getAsString(underlying, "code")
  lazy val message: Option[String] = getAsString(underlying, "message")

  def toJson = underlying
}

object Error {
  def apply(title: String, code: Option[String], message: Option[String]): Error = {
    Error(filtered(("title", title) ~ ("code" -> code) ~ ("message", message)))
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

case class Link private[collection](underlying: JObject) extends Extensible[Link] {

  def copy(obj: JObject) = Link(underlying)

  lazy val href: URI = getAsString(underlying, "href").map(URI.create(_)).getOrElse(throw new IllegalStateException("Expected href, was nothing"))
  lazy val rel: String = getAsString(underlying, "rel").getOrElse(throw new IllegalStateException("Expected rel, was nothing"))
  lazy val prompt: Option[String] = getAsString(underlying, "prompt")
  lazy val render: Option[Render] = getAsString(underlying, "render").map(Render(_))
}

object Link {
  def apply(href: URI, rel: String, prompt: Option[String] = None, render: Option[Render] = None): Link = {
    apply(
      filtered(("href" -> href.toString) ~
        ("rel" -> rel) ~
        ("prompt", prompt) ~
        ("render", render.map(_.name)))
    )
  }
}

case class Item(underlying: JObject) extends Extensible[Item] with PropertyContainer[Item] {

  lazy val href: URI = getAsString(underlying, "href").map(URI.create(_)).getOrElse(throw new IllegalStateException("Expected href, was nothing"))

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))

  lazy val links: List[Link] = getAsObjectList(underlying, "links").map(Link(_))

  def copy(obj: JObject) = Item(underlying)

  def findLinkByRel(rel: String) = links.find(_.rel == rel)

  def images = links.filter(_.render == Some(Render.IMAGE))

  protected def copyData(data: List[Property]) = copy(replace(underlying, "data", JArray(data.map(_.underlying))))

  def toTemplate = Template(data)
}

object Item {
  def apply(href: URI, data: List[Property], links: List[Link]): Item = {
    val dt = {
      val list = data.map(_.underlying)
      if (list.isEmpty) JNothing else JArray(list)
    }
    val li = {
      val list = links.map(_.underlying)
      if (list.isEmpty) JNothing else JArray(list)
    }
    apply(
      filtered(("href" -> href.toString) ~
        ("data" -> dt) ~
        ("links" -> li))
    )
  }
}

case class Query(underlying: JObject) extends Extensible[Query] with PropertyContainer[Query] {

  type T = Query

  lazy val href: URI = getAsString(underlying, "href").map(URI.create(_)).getOrElse(throw new IllegalStateException("Expected href, was nothing"))

  lazy val rel: String = getAsString(underlying, "rel").getOrElse(throw new IllegalStateException("Expected rel, was nothing"))

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))


  def copy(obj: JObject) = Query(underlying)

  protected def copyData(data: List[Property]) = copy(replace(underlying, "data", JArray(data.map(_.underlying))))

  def toURI: URI = {
    val query = data.map(p => p match {
      case vp@ValueProperty(o) => {
        "%s=%s".format(vp.name, vp.value.map(_.value.toString).getOrElse(""))
      }
      case _ => throw new IllegalArgumentException("Not a supported property type")
    }).mkString("", "&", "")
    new URI(href.getScheme, href.getUserInfo, href.getHost, href.getPort, href.getPath, query, href.getFragment)
  }
}

object Query {
  def apply(href: URI, rel: String, prompt: Option[String], data: List[Property]) : Query = {
   apply(
     filtered(("href" -> href.toString) ~
      ("rel" -> rel) ~
      ("prompt" -> prompt) ~
      ("data" -> data.map(_.underlying)))
    )
  }
}

case class Template(underlying: JObject) extends Extensible[Template] with PropertyContainer[Template] {

  def copy(obj: JObject) = Template(underlying)

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))

  protected def copyData(data: List[Property]) = copy(replace(underlying, "data", JArray(data.map(_.underlying))))
}

object Template {
  def apply(data: List[Property] = Nil): Template = apply(
    filtered(("data" -> data.map(_.underlying)))
  )
}

private[collection] sealed trait PropertyContainer[T <: PropertyContainer[T]] {
  def data: List[Property]

  def getProperty(name: String) = data.find(_.name == name)

  def getPropertyValue(name: String): Option[Value[_]] = getProperty(name).flatMap {
    case vp@ValueProperty(_) => vp.value
    case lp@ListProperty(_) => lp.value.headOption
    case _ => None
  }

  def getPropertyAsSeq(name: String): Seq[Value[_]] = {
    getProperty(name).flatMap {
      case vp@ValueProperty(_) => Some(vp.value.toSeq)
      case lp@ListProperty(_) => Some(lp.value)
      case _ => None
    }.getOrElse(Seq.empty)
  }

  def getPropertyAsMap(name: String): Map[String, Value[_]] = {
    getProperty(name).flatMap {
      case op@ObjectProperty(_) => Some(op.value)
      case _ => None
    }.getOrElse(Map.empty)
  }

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
