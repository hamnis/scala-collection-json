package net.hamnaberg.json.collection.data

import org.json4s.JsonAST._
import net.hamnaberg.json.collection._
import org.json4s.JsonAST.JArray

object JsonObjectData extends DataExtractor[JObject] with DataApply[JObject] {
  def unapply(data: List[Property]) = {
    Some(JObject(data.map { p =>
      JField(p.name, p match {
        case Property.Object(map) => JObject(map.mapValues(_.toJson).toList)
        case Property.Array(list) => JArray(list.map(_.toJson))
        case Property.Value(v) => v.toJson
      })
    }))
  }

  def apply(value: JObject) = {
    value.obj.map{
      case JField(n, v) => {
        v match {
          case JObject(fields) => ObjectProperty(n, fields.toMap.mapValues(v => Value(v).getOrElse(sys.error("Not a valid value: " + v))))
          case JArray(list) => ListProperty(n, None, list.map(v => Value(v).getOrElse(sys.error("Not a valid value: " + v))))
          case x => ValueProperty(n, None, Value(x))
        }
      }
    }
  }
}
