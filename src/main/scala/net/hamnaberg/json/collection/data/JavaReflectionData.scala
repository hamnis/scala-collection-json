package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection._
import org.json4s.{Extraction, Formats}
import org.json4s.JsonAST.JObject

class JavaReflectionData[A](implicit formats: Formats, manifest: Manifest[A]) extends DataExtractor[A] with DataApply[A]{

  def unapply(data: List[Property]): Option[A] = {
    val obj = JsonObjectData.unapply(data).getOrElse(throw new IllegalArgumentException("Cannot deserialize into json object"))
    obj.extractOpt[A]
  }

  def apply(value: A): List[Property] = {
    val json = Extraction.decompose(value)
    JsonObjectData.apply(json.asInstanceOf[JObject])
  }
}