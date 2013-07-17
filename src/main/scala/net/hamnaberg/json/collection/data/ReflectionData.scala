package net.hamnaberg.json.collection.data

import net.hamnaberg.json.collection._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => m}

class ReflectionData[A](implicit tag: scala.reflect.ClassTag[A]) extends DataExtractor[A] with DataApply[A]{
  val clazz = m.classSymbol(tag.runtimeClass)
  val cm = m.reflectClass(clazz)

  def unapply(data: List[Property]) = {

    val ctor = clazz.toType.declaration(nme.CONSTRUCTOR).asMethod
    val ctorm = cm.reflectConstructor(ctor)
    val paramList = ctor.paramss(0)
    val map = MapData.unapply(data).getOrElse(Map.empty)
    val list = paramList.map{
      s => {
        val value = map.get(s.name.decoded).getOrElse(throw new IllegalStateException("Failed to find property with name %s".format(s.name.decoded)))
        val clazz = m.runtimeClass(s.typeSignature.erasure)
        if (value.getClass == clazz) {
          value
        }
        else {
          value match {
            case e: BigDecimal if classOf[Int] == clazz => e.toInt
            case e: BigDecimal if classOf[Double] == clazz => e.toDouble
            case e: BigDecimal if classOf[String] == clazz => e.toString()
            case e => e
          }
        }
      }
    }
    Some(ctorm(list: _*).asInstanceOf[A])
  }

  def apply(value: A) = {
    val im = m.reflect(value)
    val fields = clazz.toType.declarations.filterNot(k => k.isMethod || k.isModule)
    fields.map{ f =>
      val name = f.name.decoded.trim
      val res = im.reflectField(f.asTerm).get
      Property(name, res)
    }.toList
  }
}