package net.hamnaberg.json.collection

import org.specs2.mutable.Specification
import net.hamnaberg.json.collection.Value.StringValue
import java.net.URI

class QuerySpec extends Specification {
  "Query" should {
    "be build correctly" in {
      val q = Query(URI.create("http://example.com"), "alternate", List(ValueProperty("hello", None, Some(StringValue("world")))))
      q.toURI must be equalTo(URI.create("http://example.com?hello=world"))
    }
  }

}
