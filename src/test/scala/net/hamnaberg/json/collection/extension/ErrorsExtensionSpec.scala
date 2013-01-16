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
      val errors = coll.map(_.extract(ErrorsExtension)).toOption.getOrElse(Nil)
      errors must be_!=(Nil)
    }
  }
}
