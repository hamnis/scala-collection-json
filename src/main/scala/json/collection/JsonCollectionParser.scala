package json.collection

import java.nio.charset.Charset
import java.io._


/**
 * Created by IntelliJ IDEA.
 * User: maedhros
 * Date: 12/17/11
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */

trait JsonCollectionParser {
  def parse(reader: Reader): Either[Exception, JsonCollection]
  def parse(inputStream: InputStream):Either[Exception, JsonCollection] = parse(new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))))
  def parse(string: String):Either[Exception, JsonCollection] = parse(new StringReader(string))
}