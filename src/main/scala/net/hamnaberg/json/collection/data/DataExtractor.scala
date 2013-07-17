package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection.Property

trait DataExtractor[A] {
  def unapply(data: List[Property]): Option[A]
}
