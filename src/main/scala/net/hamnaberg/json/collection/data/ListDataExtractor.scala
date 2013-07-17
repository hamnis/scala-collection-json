package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection.Property

object ListDataExtractor extends DataExtractor[List[Any]] {

  def unapply(data: List[Property]) = {
    Some(data.map {
      case p@Property.Value(v) => v.value
      case p@Property.Array(a) => a.map(_.value)
      case p@Property.Object(a) => a.mapValues(_.value)
    })
  }
}
