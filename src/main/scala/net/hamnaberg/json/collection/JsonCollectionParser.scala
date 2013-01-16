package net.hamnaberg
package json.collection

import java.io._
import java.nio.charset.Charset


/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 12/17/11
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */

trait JsonCollectionParser {
  def parseCollection(reader: Reader): Either[Throwable, JsonCollection]

  def parseCollection(inputStream: InputStream):Either[Throwable, JsonCollection] = parseCollection(new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))))

  def parseCollection(string: String):Either[Throwable, JsonCollection] = parseCollection(new StringReader(string))

  def parseTemplate(source: Reader) : Either[Throwable, Template]

  def parseTemplate(inputStream: InputStream):Either[Throwable, Template] = parseTemplate(new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))))

  def parseTemplate(string: String):Either[Throwable, Template] = parseTemplate(new StringReader(string))

}