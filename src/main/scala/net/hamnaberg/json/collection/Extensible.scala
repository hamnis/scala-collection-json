package net.hamnaberg.json.collection

import extension.Extension
import org.json4s.JObject

trait Extensible[T <: Extensible[T]] { self:T =>

  def underlying: JObject

  def copy(obj: JObject): T

  def extract[B](ext: Extension[T, B]): B = ext.apply(self)

  def apply[B](ext: Extension[T, B], value: B): T = {
    val fromExt = ext.asJson(value, self)
    if (fromExt.isEmpty) self else copy(JObject(underlying.obj ++ fromExt))
  }
}
