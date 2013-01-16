package net.hamnaberg.json.collection.extension

import net.hamnaberg.json.collection.{Extensible, Error, JsonCollection}
import org.json4s._
import net.hamnaberg.json.collection.Json4sHelpers._

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
object ErrorsExtension extends Extension[JsonCollection, List[Error]] {
  def apply(like: JsonCollection) = getAsObjectList(like.underlying, "errors").map(Error(_))

  def asJson(ext: List[Error], parent: Extensible[_]) = List(JField("errors", JArray(ext.map(_.underlying))))
}
