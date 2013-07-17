package net.hamnaberg.json.collection

trait DataExtractor[A] {
  def unapply(data: List[Property]): Option[A]
}

trait DataApply[A] {
  def apply(value: A): List[Property]
}

object MapDataExtractor extends DataExtractor[Map[String, Any]] {

  def unapply(data: List[Property]) = {
    Some(data.map {
      case p@Property.Value(v) => p.name -> v.value
      case p@Property.Array(a) => p.name -> a.map(_.value)
      case p@Property.Object(a) => p.name -> a.mapValues(_.value)
    }.toMap)
  }
}

object ListDataExtractor extends DataExtractor[List[Any]] {

  def unapply(data: List[Property]) = {
    Some(data.map {
      case p@Property.Value(v) => v.value
      case p@Property.Array(a) => a.map(_.value)
      case p@Property.Object(a) => a.mapValues(_.value)
    })
  }
}

class ReflectionData[A](implicit tag: scala.reflect.ClassTag[A]) extends DataExtractor[A] with DataApply[A]{
  import scala.reflect.runtime.universe._
  import scala.reflect.runtime.{currentMirror => m}

  def unapply(data: List[Property]) = {
    val clazz = m.classSymbol(tag.runtimeClass)
    val cm = m.reflectClass(clazz)

    val ctor = clazz.toType.declaration(nme.CONSTRUCTOR).asMethod
    val ctorm = cm.reflectConstructor(ctor)
    val paramList = ctor.paramss(0)
    val map = MapDataExtractor.unapply(data).getOrElse(Map.empty)
    val list = paramList.map{
      s => map.get(s.name.decoded).getOrElse(throw new IllegalStateException("Failed to find property with name %s".format(s.name.decoded)))
    }
    Some(ctorm(list: _*).asInstanceOf[A])
  }

  def apply(value: A) = {
    val im = m.reflect(value)
    val fields = im.symbol.toType.declarations.filterNot(k => k.isMethod || k.isModule)
    fields.map{ f =>
      val name = f.name.decoded
      val res = im.reflectField(f.asTerm).get
      res match {
        case l: Seq[_] => ListProperty(name, None, l.map(Value.toValue))
        case l: Map[_,_] => ObjectProperty(name, None, l.map{case (k: Any, v: Any) => k.toString -> Value.toValue(v)}.toMap)
        case v: Option[_] => ValueProperty(name, None, v.map(Value.toValue))
        case v => ValueProperty(name, None, Some(Value.toValue(v)))
      }
    }.toList
  }
}