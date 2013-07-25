package net.hamnaberg.json.collection.data

import org.specs2.mutable.Specification
import net.hamnaberg.json.collection.{Item, Property}

case class TestData(s: String, n: Int)

case class Person(name: String, age: Int)

class DataExtractorSpec extends Specification {
  "data extraction" should {
    "extract to list" in {
      val properties = List(Property("s", "hey"), Property("n", 23))
      ListDataExtractor.unapply(properties) should be equalTo Some(List("hey", 23))
    }
    "extract to map" in {
      val properties = List(Property("s", "hey"), Property("n", 23))
      MapData.unapply(properties) should be equalTo Some(Map("s" -> "hey", "n" -> 23))
    }
    "apply from map" in {
      val properties = List(Property("s", "hey"), Property("n", 23))
      val map = Map("s" -> "hey", "n" -> 23)
      MapData.apply(map) should be equalTo properties
    }

    "extract to TestData reflection" in {
      val properties = List(Property("s", "hey"), Property("n", 23))
      val map = TestData("hey", 23)
      implicit val formats = org.json4s.DefaultFormats
      new JavaReflectionData[TestData].unapply(properties) should be equalTo Some(map)
    }
    "apply from TestData reflection" in {
      val properties = List(Property("s", "hey"), Property("n", 23))
      val map = TestData("hey", 23)
      implicit val formats = org.json4s.DefaultFormats
      new JavaReflectionData[TestData].apply(map) should be equalTo properties
    }

    "Person to Item" in {
      implicit val formats = org.json4s.DefaultFormats
      implicit val extractor = new JavaReflectionData[Person]
      val item = Item(java.net.URI.create("hello"), Person("John", 20), Nil)
      item.getProperty("name") must be equalTo Some(Property("name", "John"))
      item.getProperty("age") must be equalTo Some(Property("age", 20))
    }
    "Person from Item" in {
      implicit val formats = org.json4s.DefaultFormats
      implicit val extractor = new JavaReflectionData[Person]
      val item = Item(java.net.URI.create("hello"), List(Property("name", "John"), Property("age", 20)), Nil)
      item.unapply[Person] must be equalTo Some(Person("John", 20))
    }

  }
}

