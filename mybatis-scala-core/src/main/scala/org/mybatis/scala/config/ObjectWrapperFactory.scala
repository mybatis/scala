/*
 * Copyright 2012 MyBatis.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mybatis.scala.config

import org.apache.ibatis.reflection.MetaObject
import org.apache.ibatis.reflection.property.PropertyTokenizer
import org.apache.ibatis.reflection.factory.{ObjectFactory => XObjectFactory}

abstract class CollectionObjectWrapper extends org.apache.ibatis.reflection.wrapper.ObjectWrapper {
  def get(prop : PropertyTokenizer) : AnyRef = null
  def set(prop : PropertyTokenizer, value : AnyRef) : Unit = {}
  def findProperty(name : String, useCamelCaseMapping : Boolean) : String = null
  def getGetterNames() : Array[String] = null
  def getSetterNames() : Array[String] = null
  def getSetterType(name : String) : Class[_] = null
  def getGetterType(name : String) : Class[_] = null
  def hasSetter(name : String) : Boolean = false
  def hasGetter(name : String) : Boolean = false
  def instantiatePropertyValue(name : String, prop : PropertyTokenizer, objectFactory : XObjectFactory) : MetaObject = null
  def isCollection() : Boolean = true
}

class ArrayBufferWrapper(buffer : scala.collection.mutable.ArrayBuffer[AnyRef]) extends CollectionObjectWrapper {
  import scala.collection.JavaConversions._
  def add(element : AnyRef) : Unit = buffer append element
  def addAll[E](elements : java.util.List[E]) : Unit = buffer.addAll(elements.asInstanceOf[java.util.Collection[AnyRef]])
}

class HashSetWrapper(set : scala.collection.mutable.HashSet[AnyRef]) extends CollectionObjectWrapper {
  import scala.collection.JavaConversions._
  def add(element : AnyRef) : Unit = set add element  
  def addAll[E](elements : java.util.List[E]) : Unit = set.addAll(elements.asInstanceOf[java.util.Collection[AnyRef]])
}

class DefaultObjectWrapperFactory extends ObjectWrapperFactory {
  def hasWrapperFor(obj : AnyRef) : Boolean = obj match {
    case o : scala.collection.mutable.ArrayBuffer[_] => true
    case o : scala.collection.mutable.HashSet[_] => true
    case _ => false
  }
  def getWrapperFor(metaObject : MetaObject, obj : AnyRef) : org.apache.ibatis.reflection.wrapper.ObjectWrapper = obj match {
    case o : scala.collection.mutable.ArrayBuffer[AnyRef] => new ArrayBufferWrapper(o)
    case o : scala.collection.mutable.HashSet[AnyRef] => new HashSetWrapper(o)
    case _ => 
      throw new IllegalArgumentException("Type not supported: " + obj.getClass.getSimpleName)
  }
}