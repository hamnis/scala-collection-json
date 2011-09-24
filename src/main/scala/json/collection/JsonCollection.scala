package json.collection

import java.net.URI

case class JsonCollection(version: Version = Version.ONE,
                          links: Seq[Link],
                          items: Seq[Item],
                          error: Option[ErrorMessage],
                          template: Option[Template],
                          queries: Seq[Query])

object JsonCollection {

  def apply(error: ErrorMessage):JsonCollection =
    JsonCollection(Version.ONE, List(), List(), Some(error), None, List())

  def apply(links: Seq[Link],
            items: Seq[Item],
            template: Option[Template],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(Version.ONE, links, items, None, template, queries)

  def apply(links: Seq[Link],
            items: Seq[Item],
            queries: Seq[Query]):JsonCollection =
    JsonCollection(Version.ONE, links, items, None, None, queries)

  def apply(links: Seq[Link],
            items: Seq[Item]):JsonCollection =
    JsonCollection(Version.ONE, links, items, None, None, List())

}

sealed class Version(id:String)

object Version {
  def apply(id: String) = id match {
    case "1.0" => ONE
    case _ => ONE
  }

  case object ONE extends Version("1.0")
}

sealed trait Property {
  def name : String
  def prompt : Option[String]
}

case class PropertyWithValue[A](name: String, prompt: Option[String], value: A) extends Property
case class PropertyWithoutValue(name: String, prompt: Option[String]) extends Property

case class ErrorMessage(title: String, code: Option[String], message: Option[String])

sealed trait Value[A] {
  def value: A
}

object Value {
  def apply(any: Any) = any match {
    case x: String => StringValue(x)
    case x: Boolean => BooleanValue(x)
    case x: Numeric[_] => NumericValue(x)
    case null => NullValue
  }
}

case class StringValue(value: String) extends Value[String]

case class NumericValue(value: Numeric[_]) extends Value[Numeric[_]]

case class BooleanValue(value: Boolean) extends Value[Boolean]

case object NullValue extends Value[Null] {
  def value = null
}

case class Link(href: URI, rel: String, prompt: Option[String])
case class Item(href: URI, properties: Seq[Property], links: Seq[Link])
case class Query(href: URI, properties: Seq[Property], links: Seq[Link])
case class Template(href: URI, rel: String, prompt: Option[String], properties: Seq[Property])

object Conversions {
  implicit def stringToValue(value: String)   = Some(Value(value))
  implicit def numericToValue(value: Numeric[_]) = Some(Value(value))
  implicit def booleanToValue(value: Boolean) = Some(Value(value))
  implicit def nullToValue(value: Null) = Some(Value(value))

  //implicit def valueToType[A](value: Option[Value[A]]) = value.map(_.value)
}
