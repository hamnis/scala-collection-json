package net.hamnaberg.json.collection

import org.specs2.mutable.Specification

class TemplateParserSpec extends Specification {
  "parsing templates" should {
    "parse correctly" in {
      val parsed = Template.parse(getClass.getResourceAsStream("/only-template.json"))
      parsed must beRight

      val template = parsed.right.get
      template.data.size must be equalTo 4
    }
  }
}
