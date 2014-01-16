package net.hamnaberg
package json.collection

import java.net.URI
import org.json4s._
import org.json4s.JsonDSL._
import Json4sHelpers._
import net.hamnaberg.json.collection.data.{DataExtractor, DataApply}

case class JsonCollection private[collection](underlying: JObject) extends Extensible[JsonCollection] with Writeable {

  val version: Version = getAsString(underlying, "version").map(Version(_)).getOrElse(Version.ONE)
  val href: URI = getAsString(underlying, "href").map(URI.create).getOrElse(throw new MissingFieldException("href", "collection"))
  val links: List[Link] = getAsObjectList(underlying, "links").map(Link(_))
  val items: List[Item] = getAsObjectList(underlying, "items").map(Item(_))
  val queries: List[Query] = getAsObjectList(underlying, "queries").map(Query(_))
  val template: Option[Template] = getAsObject(underlying, "template").map(Template(_))
  val error: Option[Error] = getAsObject(underlying, "error").map(Error(_))

  def addItem(item: Item) = addItems(List(item))

  def addItems(itemToAdd: List[Item]) = withItems(items ++ itemToAdd)

  def addLink(link: Link) = addLinks(List(link))

  def addLinks(linksToAdd: List[Link]) = withLinks(links ++ linksToAdd)

  def addQuery(query: Query) = addQueries(List(query))

  def addQueries(queriesToAdd: List[Query]) = withQueries(queries ++ queriesToAdd)

  def withTemplate(template: Template) = copy(replace(underlying, "template", template.underlying))

  def withError(error: Error) = copy(replace(underlying, "error", error.underlying))

  def withItems(links: List[Item]) = copy(replace(underlying, "items", JArray(links.map(_.underlying))))

  def withLinks(links: List[Link]) = copy(replace(underlying, "links", JArray(links.map(_.underlying))))

  def withQueries(queries: List[Query]) = copy(replace(underlying, "queries", JArray(queries.map(_.underlying))))

  def isError = error.isDefined

  def map[B](f: (Item) => B): List[B] = items.map(f)
  
  def flatMap[B](f: (Item) => List[B]): List[B] = items.flatMap(f)

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

  def copy(obj: JObject) = JsonCollection(obj)

  def toJson = JObject(List(JField("collection", underlying)))
}

trait Writeable extends ToJson {
  import org.json4s.native.JsonMethods
  import org.json4s.native.Printer

  def writeTo[A <: java.io.Writer](writer: A, pretty: Boolean = false): A = {
    val json = toJson
    if (pretty) {
      Printer.pretty(JsonMethods.render(json), writer)
    }
    else {
      Printer.compact(JsonMethods.render(json), writer)
    }
  }

  override def toString = writeTo(new java.io.StringWriter(), pretty = true).toString

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


  def parse(reader: java.io.Reader)(implicit parser: JsonCollectionParser): Either[Throwable, JsonCollection] = parser.parseCollection(reader)

  def parse(inputStream: java.io.InputStream)(implicit parser: JsonCollectionParser):Either[Throwable, JsonCollection] = parser.parseCollection(inputStream)

  def parse(string: String)(implicit parser: JsonCollectionParser):Either[Throwable, JsonCollection] = parser.parseCollection(string)

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

case class Error(underlying: JObject) extends Extensible[Error] with ToJson {

  def copy(obj: JObject) = Error(obj)

  lazy val title: String = getAsString(underlying, "title").getOrElse(throw new MissingFieldException("title", "error"))
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

  def copy(obj: JObject) = Link(obj)

  lazy val href: URI = getAsString(underlying, "href").map(URI.create).getOrElse(throw new MissingFieldException("href", "link"))
  lazy val rel: String = getAsString(underlying, "rel").getOrElse(throw new MissingFieldException("rel", "link"))
  lazy val name: Option[String] = getAsString(underlying, "name")
  lazy val prompt: Option[String] = getAsString(underlying, "prompt")
  lazy val render: Option[Render] = getAsString(underlying, "render").map(Render(_))
}

object Link {
  def apply(href: URI, rel: String, prompt: Option[String] = None, name: Option[String] = None, render: Option[Render] = None): Link = {
    apply(
      filtered(("href" -> href.toString) ~
        ("rel" -> rel) ~
        ("name", name) ~
        ("prompt", prompt) ~
        ("render", render.map(_.name)))
    )
  }
}

case class Item private[collection](underlying: JObject) extends Extensible[Item] with PropertyContainer[Item] {

  lazy val href: URI = getAsString(underlying, "href").map(URI.create).getOrElse(throw new MissingFieldException("href", "item"))

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))

  lazy val links: List[Link] = getAsObjectList(underlying, "links").map(Link(_))

  def copy(obj: JObject) = Item(obj)

  def findLinkByRel(rel: String) = links.find(_.rel == rel)

  def images: List[Link] = links.filter(_.render == Some(Render.IMAGE))

  def addLink(link: Link) = addLinks(List(link))

  def addLinks(linksToAdd: List[Link]) = withLinks(links ++ linksToAdd)

  def withLinks(links: List[Link]) = copy(replace(underlying, "links", JArray(links.map(_.underlying))))

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

  def apply[A](href: URI, value: A, links: List[Link])(implicit data: DataApply[A]): Item = {
    apply(href, data.apply(value), links)
  }
}

case class Query private[collection](underlying: JObject) extends Extensible[Query] with PropertyContainer[Query] {

  type T = Query

  lazy val href: URI = getAsString(underlying, "href").map(URI.create).getOrElse(throw new MissingFieldException("href", "query"))

  lazy val rel: String = getAsString(underlying, "rel").getOrElse(throw new MissingFieldException("rel", "query"))

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))

  lazy val name: Option[String] = getAsString(underlying, "name")

  def copy(obj: JObject) = Query(obj)

  protected def copyData(data: List[Property]) = copy(replace(underlying, "data", JArray(data.map(_.underlying))))

  def toURI: URI = {
    val query = data.map(p => p match {
      case vp@ValueProperty(o) => {
        "%s=%s".format(vp.name, vp.asValue.map(_.value.toString).getOrElse(""))
      }
      case _ => throw new IllegalArgumentException("Not a supported property type")
    }).mkString("", "&", "")
    new URI(href.getScheme, href.getUserInfo, href.getHost, href.getPort, href.getPath, query, href.getFragment)
  }
}

object Query {
  def apply(href: URI, rel: String, data: List[Property] = Nil, prompt: Option[String] = None, name: Option[String] = None) : Query = {
   apply(
     filtered(("href" -> href.toString) ~
      ("rel" -> rel) ~
      ("prompt" -> prompt) ~
      ("name", name) ~
      ("data" -> data.map(_.underlying)))
    )
  }
}

case class Template private[collection](underlying: JObject) extends Extensible[Template] with PropertyContainer[Template] with Writeable {

  def copy(obj: JObject) = Template(obj)

  lazy val data: List[Property] = getAsObjectList(underlying, "data").map(Property(_))

  protected def copyData(data: List[Property]) = copy(replace(underlying, "data", JArray(data.map(_.underlying))))

  def toJson = filtered(
      "template" -> ("data" -> data.map(_.underlying))
  )
}

object Template {
  def apply(data: List[Property]): Template = apply(
    filtered("data" -> data.map(_.underlying))
  )

  def apply[A](value: A)(implicit data: DataApply[A]): Template = apply(data.apply(value))

  def apply(data: Property*): Template = apply(data.toList)

  def parse(input: java.io.Reader)(implicit parser: JsonCollectionParser): Either[Throwable, Template] = parser.parseTemplate(input)

  def parse(input: java.io.InputStream)(implicit parser: JsonCollectionParser): Either[Throwable, Template] = parser.parseTemplate(input)

  def parse(input: String)(implicit parser: JsonCollectionParser): Either[Throwable, Template] = parser.parseTemplate(input)
}

private[collection] sealed trait PropertyContainer[T <: PropertyContainer[T]] {
  def data: List[Property]

  def unapply[A](implicit extractor: DataExtractor[A]) = extractor.unapply(data)

  def getProperty(name: String) = data.find(_.name == name)

  def getPropertyValue(name: String): Option[Value[_]] = getProperty(name).flatMap {
    case vp@ValueProperty(_) => vp.asValue
    case lp@ListProperty(_) => lp.asArray.headOption
    case _ => None
  }

  def getPropertyAsSeq(name: String): Seq[Value[_]] = {
    getProperty(name).flatMap {
      case vp:ValueProperty => Some(vp.asValue.toSeq)
      case lp:ListProperty => Some(lp.asArray)
      case _ => None
    }.getOrElse(Seq.empty)
  }

  def getPropertyAsMap(name: String): Map[String, Value[_]] = {
    getProperty(name).flatMap {
      case op:ObjectProperty => Some(op.asObject)
      case _ => None
    }.getOrElse(Map.empty)
  }

  def addProperty(property: Property) = copyData(data ::: List(property))

  def replaceProperty(property: Property) = {
    val index = data.indexWhere(_.name == property.name)
    if (index == -1) {
      throw new NoSuchElementException("No property with name %s found to replace".format(property.name))
    }
    else {
      copyData(data.updated(index, property))
    }
  }

  def removeProperty(name: String) = copyData(data.filterNot(_.name == name))

  protected def copyData(data: List[Property]) : T
}


class MissingFieldException(name: String, parent: String) extends RuntimeException("Missing field '%s' in '%s'".format(name, parent))
