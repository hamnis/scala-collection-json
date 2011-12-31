package net.hamnaberg
package json.collection

import org.specs2.mutable.Specification
import java.io.InputStreamReader
import java.net.URI
import net.liftweb.json.JsonAST.JString

class JsonParserSpec extends Specification {
  val parser = LiftJsonCollectionParser
  val href = URI.create("http://example.org/friends/")
  "parsing json " should {
    "minimal" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/minimal.json")))
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, None))
      }
    }
    "minimal without version" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/minimal-without-version.json")))
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, None))
      }
    }

    "one item" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/item.json")))
      val item = Item(
        URI.create("http://example.org/friends/jdoe"),
        List(
          Property("full-name", Some("Full Name"), Some(JString("J. Doe"))),
          Property("email", Some("Email"), Some(JString("jdoe@example.org")))
        ),
        List(
          Link(URI.create("http://examples.org/blogs/jdoe"), "blog", Some("Blog")),
          Link(URI.create("http://examples.org/images/jdoe"), "avatar", Some("Avatar"), Some(Render.IMAGE))
        )
        )
      val links = List(
        Link(URI.create("http://example.org/friends/rss"), "feed"),
        Link(URI.create("http://example.org/friends/?queries"), "queries"),
        Link(URI.create("http://example.org/friends/?template"), "template")
      )
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, links, List(item), Nil, None, None))
      }

    }

    "links" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/links.json")))
      val links = List(
        Link(URI.create("http://example.org/friends/rss"), "feed"),
        Link(URI.create("http://example.org/friends/?queries"), "queries"),
        Link(URI.create("http://example.org/friends/?template"), "template")
      )
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, links, Nil, Nil, None, None))
      }
    }

    "queries" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/queries.json")))
      val queries = List(
        Query(URI.create("http://example.org/friends/search"), "search", Some("Search"), List(Property("search", None, Some(JString("")))))
      )
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, Nil, Nil, queries, None, None))
      }
    }

    "template" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/template.json")))
      val template = Some(Template(List(
        Property("full-name", Some("Full Name"), Some(JString(""))),
        Property("email", Some("Email"), Some(JString(""))),
        Property("blog", Some("Blog"), Some(JString(""))),
        Property("avatar", Some("Avatar"), Some(JString("")))
        )))
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, Nil, Nil, Nil, template, None))
      }

    }
    "error" in {
      val result = parser.parseCollection(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/error.json")))
      val error = Some(ErrorMessage("Server Error", Some("X1C2"), Some("The server have encountered an error, please wait and try again.")))
      result match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(JsonCollection(Version.ONE, href, Nil, Nil, Nil, None, error))
      }
    }
    "generated is the same as parsed" in {
      val item = Item(
        URI.create("http://example.org/friends/jdoe"),
        List(
          Property("full-name", Some("Full Name"), Some(JString("J. Doe"))),
          Property("email", Some("Email"), Some(JString("jdoe@example.org")))
        ),
        List(
          Link(URI.create("http://examples.org/blogs/jdoe"), "blog", Some("Blog")),
          Link(URI.create("http://examples.org/images/jdoe"), "avatar", Some("Avatar"), Some(Render.IMAGE))
        )
      )
      val links = List(
        Link(URI.create("http://example.org/friends/rss"), "feed"),
        Link(URI.create("http://example.org/friends/?queries"), "queries"),
        Link(URI.create("http://example.org/friends/?template"), "template")
      )
      val expected = JsonCollection(item.href, links, item)
      import net.liftweb.json._
      val rendered = compact(render(expected.toJson))
      val parsed = parser.parseCollection(rendered)
      
      parsed match {
        case Left(ex) => throw ex
        case Right(v) => v must beEqualTo(expected)
      }
    }
  }
}
