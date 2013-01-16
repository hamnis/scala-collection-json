package net.hamnaberg
package json.collection

import java.io.Reader
import util.control.Exception.allCatch
import org.json4s._
import org.json4s.native._
import Json4sHelpers._

object NativeJsonCollectionParser extends JsonCollectionParser {

  def parseCollection(reader: Reader): Either[Throwable, JsonCollection] = {
    try {
      val parsed = JsonParser.parse(reader, true)
      parsed match {
        case JObject(List(JField("collection", x@JObject(_)))) => {
          allCatch.either(
            JsonCollection(
              if (getAsString(x, "version") != Some("1.0")) {
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
        case JObject(List(JField("template", x: JObject))) => allCatch.either(Template(filtered(x)))
        case _ => throw new IllegalArgumentException("Unexpected json here. was\n %s".format(parsed))
      }
    }
    catch {
      case e : Throwable => Left(e)
    }
  }
}
