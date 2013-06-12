package net.hamnaberg.json.collection

import org.specs2.mutable.Specification
import java.net.URI

class CollectionSpec extends Specification {
  "A collection" should {
    "create without anything" in {
      val coll = JsonCollection(URI.create("hello"))
      coll.queries must beEmpty
      coll.links must beEmpty
      coll.items must beEmpty
      coll.href must beEqualTo(URI.create("hello"))
    }
    "add query after creation" in {
      val coll = JsonCollection(URI.create("hello")).addQuery(Query(URI.create("pp"), "hello"))
      coll.queries.size must beEqualTo(1)
    }
  }
}
