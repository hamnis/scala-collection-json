package json.collection

import org.specs2.mutable.Specification
import java.io.InputStreamReader

class JsonParserSpec extends Specification {
  val parser = new JsonParser()
  "parsing json " should {
    "minimal is successful" in {
      val collection = parser.parse(new InputStreamReader(classOf[JsonParserSpec].getResourceAsStream("/minimal.json")))
      println(collection)
      1 must beEqualTo(1)
    }
  }
}
