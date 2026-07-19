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

import org.apache.ibatis.reflection.property.PropertyTokenizer
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util

class ObjectWrapperFactorySpec extends AnyFunSpec with Matchers {

  describe("DefaultObjectWrapperFactory") {
    it("should report wrapper support for mutable ArrayBuffer and HashSet only") {
      val factory = new DefaultObjectWrapperFactory()

      factory.hasWrapperFor(scala.collection.mutable.ArrayBuffer.empty[AnyRef]) shouldBe true
      factory.hasWrapperFor(scala.collection.mutable.HashSet.empty[AnyRef]) shouldBe true
      factory.hasWrapperFor("not-a-collection") shouldBe false
    }

    it("should wrap mutable ArrayBuffer and support add/addAll") {
      val factory = new DefaultObjectWrapperFactory()
      val target = scala.collection.mutable.ArrayBuffer.empty[AnyRef]
      val wrapper = factory.getWrapperFor(null, target)

      wrapper shouldBe a[ArrayBufferWrapper]
      wrapper.isCollection() shouldBe true

      wrapper.add("a")
      wrapper.addAll(util.Arrays.asList("b", "c"))
      target should contain inOrderOnly ("a", "b", "c")
    }

    it("should wrap mutable HashSet and support add/addAll") {
      val factory = new DefaultObjectWrapperFactory()
      val target = scala.collection.mutable.HashSet.empty[AnyRef]
      val wrapper = factory.getWrapperFor(null, target)

      wrapper shouldBe a[HashSetWrapper]
      wrapper.isCollection() shouldBe true

      wrapper.add("a")
      wrapper.addAll(util.Arrays.asList("b", "c"))
      target should contain allOf ("a", "b", "c")
    }

    it("should throw IllegalArgumentException for unsupported types") {
      val factory = new DefaultObjectWrapperFactory()

      val error = the[IllegalArgumentException] thrownBy factory.getWrapperFor(null, "bad".asInstanceOf[AnyRef])
      error.getMessage should include("Type not supported")
    }
  }

  describe("CollectionObjectWrapper defaults") {
    it("should return defaults for unsupported metadata operations") {
      val wrapper = new ArrayBufferWrapper(scala.collection.mutable.ArrayBuffer.empty[AnyRef])
      val property = new PropertyTokenizer("name")

      wrapper.get(property) shouldBe null
      wrapper.findProperty("name", useCamelCaseMapping = true) shouldBe null
      wrapper.getGetterNames() shouldBe null
      wrapper.getSetterNames() shouldBe null
      wrapper.getSetterType("name") shouldBe null
      wrapper.getGetterType("name") shouldBe null
      wrapper.hasSetter("name") shouldBe false
      wrapper.hasGetter("name") shouldBe false
      wrapper.instantiatePropertyValue("name", property, null) shouldBe null

      noException shouldBe thrownBy(wrapper.set(property, "v"))
    }
  }
}
