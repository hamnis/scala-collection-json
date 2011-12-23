package net.hamnaberg
package json.collection

import java.nio.charset.Charset
import java.io._
import io.Codec


/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 12/17/11
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */

trait JsonCollectionParser {
  def parseCollection(reader: Reader): Either[Exception, JsonCollection]

  def parseCollection(inputStream: InputStream):Either[Exception, JsonCollection] = parseCollection(new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))))

  def parseCollection(string: String):Either[Exception, JsonCollection] = parseCollection(new StringReader(string))

  def parseTemplate(source: Reader) : Either[Exception, Template]

  def parseTemplate(inputStream: InputStream)(implicit codec:Codec = Codec(Codec.UTF8)):Either[Exception, Template] = parseTemplate(new BufferedReader(new InputStreamReader(inputStream, codec.charSet)))

  def parseTemplate(string: String):Either[Exception, Template] = parseTemplate(new StringReader(string))

}