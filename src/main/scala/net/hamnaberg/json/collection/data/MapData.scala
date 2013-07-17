package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection.Property

object MapData extends DataExtractor[Map[String, Any]] with DataApply[Map[String, Any]] {

  def unapply(data: List[Property]) = {
    Some(data.map {
      case p@Property.Value(v) => p.name -> v.value
      case p@Property.Array(a) => p.name -> a.map(_.value)
      case p@Property.Object(a) => p.name -> a.mapValues(_.value)
    }.toMap)
  }

  def apply(value: Map[String, Any]) = value.map{case (k,v) => Property.apply(k, v)}.toList
}
