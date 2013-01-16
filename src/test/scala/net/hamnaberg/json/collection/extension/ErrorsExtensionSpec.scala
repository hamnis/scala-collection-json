package net.hamnaberg.json.collection.extension

import org.specs2.mutable.Specification
import net.hamnaberg.json.collection._

/**
 * @author Erlend Hamnaberg<erlend.hamnaberg@arktekk.no>
 */
class ErrorsExtensionSpec extends Specification {
  "Errors extension " should {
    "be deserialized" in {
      val coll = NativeJsonCollectionParser.parseCollection(getClass.getResourceAsStream("/errors.json")).right
      val errors: List[Error] = coll.map(_.extract(ErrorsExtension)).right.getOrElse(Nil)
      errors must not beEmpty
    }
  }
}
