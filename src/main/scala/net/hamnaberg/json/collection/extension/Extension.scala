package net.hamnaberg.json.collection.extension

import net.hamnaberg.json.collection.Extensible
import net.hamnaberg.json.lift.JsonAST.JField

trait Extension[Like, A] { self =>
  def apply(like: Like): A
  def unapply(like: Like) : Option[A] = Some(apply(like))
  def asJson(ext: A, parent: Extensible[_]): Seq[JField]

  def ++[B](b: Extension[Like, B]): Extension[Like, (A, B)] = new Extension[Like, (A, B)] {
    def apply(like: Like) = self.apply(like) -> b.apply(like)

    def asJson(ext: (A, B), parent: Extensible[_]) = self.asJson(ext._1, parent) ++ b.asJson(ext._2, parent)
  }
}
