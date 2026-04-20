/*
 *    Copyright 2011-2026 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.scala.config

import org.apache.ibatis.reflection.ReflectionException
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util

class DefaultObjectFactorySpec extends AnyFunSpec with Matchers {

  describe("DefaultObjectFactory.create") {
    it("should resolve Java collection interfaces to default concrete classes") {
      val factory = new DefaultObjectFactory()

      factory.create(classOf[util.List[?]]) shouldBe a[util.LinkedList[?]]
      factory.create(classOf[util.Collection[?]]) shouldBe a[util.LinkedList[?]]
      factory.create(classOf[util.Map[?, ?]]) shouldBe a[util.HashMap[?, ?]]
      factory.create(classOf[util.SortedSet[?]]) shouldBe a[util.TreeSet[?]]
      factory.create(classOf[util.Set[?]]) shouldBe a[util.HashSet[?]]
    }

    it("should resolve Scala collection interfaces to default mutable implementations") {
      val factory = new DefaultObjectFactory()

      factory.create(classOf[scala.collection.Seq[?]]) shouldBe a[scala.collection.mutable.ArrayBuffer[?]]
      factory.create(classOf[scala.collection.Map[?, ?]]) shouldBe a[scala.collection.mutable.HashMap[?, ?]]
      factory.create(classOf[scala.collection.Set[?]]) shouldBe a[scala.collection.mutable.HashSet[?]]
    }

    it("should instantiate classes with a matching constructor and arguments") {
      val factory = new DefaultObjectFactory()
      val argTypes = util.Arrays.asList(classOf[String].asInstanceOf[Class[?]])
      val args = util.Arrays.asList("abc".asInstanceOf[AnyRef])

      val created = factory.create(classOf[DefaultObjectFactorySpec.WithArgs], argTypes, args)
      created.value shouldBe "abc"
    }

    it("should throw ReflectionException for invalid constructor argument types") {
      val factory = new DefaultObjectFactory()
      val argTypes = util.Arrays.asList(classOf[java.lang.Integer].asInstanceOf[Class[?]])
      val args = util.Arrays.asList("abc".asInstanceOf[AnyRef])

      val error = the[ReflectionException] thrownBy factory.create(classOf[DefaultObjectFactorySpec.WithArgs], argTypes, args)
      error.getMessage should include("Error instantiating")
    }
  }

  describe("DefaultObjectFactory.getConstructor") {
    it("should get the no-arg constructor when args are null") {
      val factory = new DefaultObjectFactory()
      val constructor = factory.getConstructor(classOf[DefaultObjectFactorySpec.EmptyClass], null)

      constructor should not be null
      constructor.getParameterCount shouldBe 0
    }

    it("should throw ReflectionException for an invalid constructor signature") {
      val factory = new DefaultObjectFactory()
      val error = the[ReflectionException] thrownBy factory.getConstructor(classOf[DefaultObjectFactorySpec.EmptyClass], Array(classOf[String]))

      error.getMessage should include("Error instantiating")
    }
  }

  describe("DefaultObjectFactory.isCollection") {
    it("should detect Scala seq and set as collection") {
      val factory = new DefaultObjectFactory()

      factory.isCollection(classOf[scala.collection.mutable.ArrayBuffer[?]]) shouldBe true
      factory.isCollection(classOf[scala.collection.mutable.HashSet[?]]) shouldBe true
      factory.isCollection(classOf[DefaultObjectFactorySpec.EmptyClass]) shouldBe false
    }
  }

  describe("DefaultObjectFactory.CacheKey") {
    it("should implement hashCode and equals for same class and arguments") {
      val factory = new DefaultObjectFactory()

      val k1 = new factory.CacheKey(classOf[String], Array(classOf[Int], classOf[Long]))
      val k2 = new factory.CacheKey(classOf[String], Array(classOf[Int], classOf[Long]))
      val k3 = new factory.CacheKey(classOf[String], null)

      k1 shouldBe k2
      k1.hashCode shouldBe k2.hashCode
      k1 should not equal k3
      k1.equals(null) shouldBe false
    }
  }
}

object DefaultObjectFactorySpec {
  class EmptyClass
  class WithArgs(val value: String)
}
