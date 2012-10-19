package net.hamnaberg
package json.collection

import java.io.Reader
import util.control.Exception.allCatch
import net.hamnaberg.json.lift.JsonParser
import net.hamnaberg.json.lift.JsonAST._

/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 11/23/11
 *
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */

object LiftJsonCollectionParser extends JsonCollectionParser {

  def parseCollection(reader: Reader): Either[Throwable, JsonCollection] = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parsed match {
        case JObject(List(JField("collection", x@JObject(_)))) => {
          allCatch.either(
            JsonCollection(
              if (x.getAsString("version") != Some("1.0")) {
                JObject(JField("version", JString("1.0")) :: x.obj)
              } else {
                x
              })
          )
        }
        case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(parsed))
      }
    }
    catch {
      case e : Throwable => Left(e)
    }
  }


  def parseTemplate(reader: Reader): Either[Throwable, Template] = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parsed match {
        case JObject(List(JField("template", x: JObject))) => allCatch.either(Template(x.filtered))
        case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(parsed))
      }
    }
    catch {
      case e : Throwable => Left(e)
    }
  }
}
