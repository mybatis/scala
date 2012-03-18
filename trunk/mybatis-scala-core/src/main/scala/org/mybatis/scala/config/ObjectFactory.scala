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

class ObjectFactory extends org.apache.ibatis.reflection.factory.ObjectFactory {

  def create[T](t : Class[T]) : T = create(t, null, null)

  def create[T](t : Class[T], constructorArgTypes : java.util.List[Class[_]], constructorArgs : java.util.List[AnyRef]) : T = {
    val classToCreate = resolveInterface(t)
    instantiateClass[T](classToCreate, constructorArgTypes, constructorArgs)
  }

  def setProperties(properties : java.util.Properties) : Unit = {}

  private def instantiateClass[T](t : Class[_], constructorArgTypes : java.util.List[Class[_]], constructorArgs : java.util.List[AnyRef]) : T = {
    
    val argTypes = {
      if (constructorArgTypes != null) 
        constructorArgTypes.toArray[Class[_]](new Array[Class[_]](constructorArgTypes.size))
      else
        null
    }
    
    val argValues = {
      if (constructorArgs != null) 
        constructorArgs.toArray[AnyRef](new Array[AnyRef](constructorArgs.size))
      else 
        null
    }
    
    try {
      if (argTypes == null || argValues == null) {
        val constructor = t.getDeclaredConstructor()
        if (!constructor.isAccessible()) {
          constructor.setAccessible(true)
        }
        constructor.newInstance().asInstanceOf[T]
      }
      else {
        val constructor = t.getDeclaredConstructor(argTypes : _*)
        if (!constructor.isAccessible()) {
          constructor.setAccessible(true)
        }
        constructor.newInstance(argValues : _*).asInstanceOf[T]
      }      
    }
    catch {
      case e : Exception =>
        val types = {
          if (argTypes == null) ""
          else argTypes.map(_.getSimpleName).reduceLeft(_ + ", " + _)
        }
        val values = {
          if (argValues == null) ""
          else argValues.map(String.valueOf(_)).reduceLeft(_ + ", " + _)
        }
        throw new org.apache.ibatis.reflection.ReflectionException(
          "Error instantiating %s with invalid types (%s) or values (%s). Cause: %s".format(t.getSimpleName, types, values, e.getMessage), e);
    }
  }

  private def resolveInterface[T](t : Class[T]) : Class[_] = {
    // Java Collections
    if (t == classOf[java.util.List[_]])
      classOf[java.util.LinkedList[_]]
    else if (t == classOf[java.util.Collection[_]])
      classOf[java.util.LinkedList[_]]
    else if (t == classOf[java.util.Map[_,_]])
      classOf[java.util.HashMap[_,_]]
    else if (t == classOf[java.util.SortedSet[_]])
      classOf[java.util.TreeSet[_]]
    else if (t == classOf[java.util.Set[_]])
      classOf[java.util.HashSet[_]]
    // Scala Collections
    else if (t == classOf[scala.collection.Seq[_]])
      classOf[scala.collection.mutable.ArrayBuffer[_]]
    else if (t == classOf[scala.collection.Map[_,_]])
      classOf[scala.collection.mutable.HashMap[_,_]]
    else if (t == classOf[scala.collection.Set[_]])
      classOf[scala.collection.mutable.HashSet[_]]
    else {
      t
    }
  }
    
  def isCollection[T](t : Class[T]) : Boolean = 
    classOf[scala.collection.Seq[_]].isAssignableFrom(t) ||
    classOf[scala.collection.Set[_]].isAssignableFrom(t)

}
