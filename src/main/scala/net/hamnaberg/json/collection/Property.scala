package net.hamnaberg.json.collection

import net.hamnaberg.json.collection.Json4sHelpers._
import org.json4s._
import org.json4s.JsonDSL._
import scala.Some


sealed trait Property extends Extensible[Property] {
  lazy val name: String = getAsString(underlying, "name").getOrElse(throw new MissingFieldException("name", "property"))
  lazy val prompt: Option[String] = getAsString(underlying, "prompt")

  def isValue: Boolean = false
  def isArray: Boolean = false
  def isObject: Boolean = false

  def asValue: Option[Value[_]] = None
  def asArray: List[Value[_]] = Nil
  def asObject: Map[String, Value[_]] = Map.empty
}

object Property {
  def apply(obj: JObject): Property = {
    val map = obj.values
    obj match {
      case o if map.contains("array") => ListProperty(o)
      case o if map.contains("object") => ObjectProperty(o)
      case _ => ValueProperty(obj)
    }
  }
}

case class ValueProperty private[collection](underlying: JObject) extends Property {
  def copy(obj: JObject) = ValueProperty(obj)

  override val isValue = true

  override val asValue = (underlying \ "value").toOption.flatMap(Value(_))
}

object ValueProperty {
  def apply(name: String, prompt: Option[String] = None, value: Option[Value[_]] = None): ValueProperty = {
    apply(
      filtered(("name" -> name) ~
        ("prompt" -> prompt) ~
        ("value" -> value.map(_.toJson)))
    )
  }
  def apply[A](name: String, prompt: Option[String], value: Option[A])(implicit converter: ValueConverter[A, _]): ValueProperty = {
    apply(name, prompt, value.map(converter.convert))
  }

  def apply[A](name: String, value: Option[A])(implicit converter: ValueConverter[A, _]): ValueProperty = apply(name, None, value)
}

case class ListProperty private[collection](underlying: JObject) extends Property {
  def copy(obj: JObject) = ListProperty(obj)

  override val isArray = true

  override val asArray = getAsValueList(underlying, "array").flatMap(Value(_))
}

object ListProperty {
  def apply(name: String, prompt: Option[String] = None, value: Seq[Value[_]] = Nil): ListProperty = {
    apply(
      filtered(("name" -> name) ~
        ("prompt" -> prompt) ~
        ("array" -> value.map(_.toJson)))
    )
  }

  def apply[A](name: String, prompt: Option[String], value: Seq[A])(implicit converter: ValueConverter[A, _]): ListProperty =
    apply(name, prompt, value.map(converter.convert))

  def apply[A](name: String, value: Seq[A])(implicit converter: ValueConverter[A, _]): ListProperty = apply(name, None, value)
}

case class ObjectProperty private[collection](underlying: JObject) extends Property {
  def copy(obj: JObject) = ObjectProperty(obj)

  override val isObject = true

  override val asObject = getAsObject(underlying, "object").map(obj => {
    val values : Seq[(String, Option[Value[_]])] = obj.obj.map{case (n,v) => n -> Value(v)}
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