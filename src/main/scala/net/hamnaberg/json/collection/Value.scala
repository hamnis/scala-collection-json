package net.hamnaberg.json.collection

import org.json4s._

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

  private[collection] def toValue(any: Any): Value[_] = {
    import util.control.Exception.allCatch

    def toNumberValue(n: Any) = allCatch.opt(NumberValue(BigDecimal(n.toString))).getOrElse(throw new IllegalArgumentException(n + " is not a number"))

    any match {
      case null => NullValue
      case v: String => StringValue(v)
      case v: Boolean => BooleanValue(v)
      case v => toNumberValue(v)
    }
  }
}

trait ValueConverter[-A, B] {
  def convert(input: A): Value[B]
}

object ValueConverter {
  import Value._

  implicit def stringToValue = new ValueConverter[String, String] {
    def convert(s: String) = StringValue(s)
  }
  implicit def numericToValue[A: Numeric] = new ValueConverter[A, BigDecimal] {
    def convert(s: A) = NumberValue(BigDecimal(s.toString))
  }
  implicit def booleanToValue = new ValueConverter[Boolean, Boolean] {
    def convert(s: Boolean) = BooleanValue(s)
  }
  implicit def nullToValue = new ValueConverter[Null, Null] {
    def convert(s: Null) = NullValue
  }
}