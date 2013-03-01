package net.hamnaberg.json.collection

import org.json4s._

object Json4sHelpers {
  def getAsObjectList(obj: JObject, name: String): List[JObject] = {
    (obj \ name) match {
      case JArray(values) => values.collect{case o@JObject(_) => o}
      case o@JObject(_) => List(o)
      case _ => Nil
    }
  }

  def getAsValueList(obj: JObject, name: String): List[JValue] = {
    (obj \ name) match {
      case JNothing => Nil
      case JArray(values) => values
      case j => List(j)
    }
  }

  def getAsObject(obj: JObject, name: String): Option[JObject] = {
    (obj \ name) match {
      case o@JObject(_) => Some(o)
      case _ => None
    }
  }

  def getAsString(obj: JObject, name: String): Option[String] = {
    (obj \ name) match {
      case JString(s) => Some(s)
      case _ => None
    }
  }

  def replace(obj: JObject, name: String, value: JValue): JObject = {
    val map = obj.obj.toMap - name
    JObject(map.updated(name, value).toList)
  }

  def filtered(obj: JObject): JObject = {
    JObject(obj.obj.filter {
      case JField(_, JNothing) => false
      case _ => true
    })
  }
}
