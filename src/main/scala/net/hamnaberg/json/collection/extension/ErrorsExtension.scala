package net.hamnaberg.json.collection.extension

import net.hamnaberg.json.collection.{Extensible, ErrorMessage, JsonCollection}
import net.hamnaberg.json.lift._

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
object ErrorsExtension extends Extension[JsonCollection, List[ErrorMessage]] {
  def apply(like: JsonCollection) = like.underlying.getAsList("errors").map(jt => ErrorMessage(jt.asInstanceOf[JObject]))

  def asJson(ext: List[ErrorMessage], parent: Extensible[_]) = List(JField("errors", JArray(ext.map(_.underlying))))
}
