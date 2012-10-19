package net.hamnaberg.json.collection

import extension.Extension
import net.hamnaberg.json.lift.JsonAST.JObject

trait Extensible[T <: Extensible[T]] { self:T =>

  def underlying: JObject

  def copy(obj: JObject): T

  def extract[B](ext: Extension[T, B]): B = ext.apply(self)

  def apply[B](ext: Extension[T, B], value: B): T = copy(JObject(underlying.obj ++ ext.asJson(value, self)))
}
