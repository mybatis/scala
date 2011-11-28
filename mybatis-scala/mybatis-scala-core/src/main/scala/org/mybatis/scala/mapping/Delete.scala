/*
 * Copyright 2011 The myBatis Team
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

package org.mybatis.scala.mapping

import org.mybatis.scala.session.Session

abstract class Delete [Param : Manifest] extends Statement {

  def parameterTypeClass = manifest[Param].erasure

  def apply()(implicit s : Session) : Int = {
    s.delete(fqi.id)
  }

  def apply(param : Param)(implicit s : Session) : Int = {
    s.delete(fqi.id, param)
  }

}
