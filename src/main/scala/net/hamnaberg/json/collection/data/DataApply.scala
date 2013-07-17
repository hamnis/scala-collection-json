package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection.Property

trait DataApply[A] {
  def apply(value: A): List[Property]
}
