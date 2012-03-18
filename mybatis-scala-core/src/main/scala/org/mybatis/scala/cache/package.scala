/*
 * Copyright 2011-2012 The myBatis Team
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

package org.mybatis.scala

import org.apache.ibatis.cache.{Cache => MBCache}
import org.apache.ibatis.cache.impl.PerpetualCache
import org.mybatis.scala.mapping.T
import org.apache.ibatis.cache.decorators._

/** Provides Cache supporting types and objects */
package object cache {

  /** Alias of [[org.apache.ibatis.cache.Cache]] */
  type Cache = MBCache

  /** Default MyBatis cache implementation: PerpetualCache */
  val DefaultCache = T[PerpetualCache]

  /** Default eviction policies */
  object Eviction {

    /** Least Recently Used:  Removes objects that haven’t been used for the longest period of time. */
    val LRU   = T[LruCache]

    /** First In First Out:  Removes objects in the order that they entered the cache.  */
    val FIFO  = T[FifoCache]

    /** Soft Reference:  Removes objects based on the garbage collector state and the rules of Soft References. */
    val SOFT  = T[SoftCache]

    /** Weak Reference:  More aggressively than Soft, removes objects based on the garbage collector state and rules of Weak References. */
    val WEAK  = T[WeakCache]

  }

}