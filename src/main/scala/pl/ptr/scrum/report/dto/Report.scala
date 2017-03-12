// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package pl.ptr.scrum.report.dto

import java.util.Date

import pl.ptr.scrum.report.dto.Types.{ProjectName, StatusName, TypeName}
import pl.ptr.scrum.report.utils.TypeMagic._

import scala.beans.BeanProperty
import scala.collection.immutable.Map


case class DayValue(
                     @BeanProperty
                     statusHours: Map[StatusName, Double] = Map(),
                     @BeanProperty
                     projectsMap: Map[ProjectName, Map[TypeName, Double]] = Map())

case class Report(
                   @BeanProperty
                   sprintNumber: Int,
                   @BeanProperty
                   dateFrom: Date,
                   @BeanProperty
                   dateTo: Date,
                   @BeanProperty
                   team: String,
                   @BeanProperty
                   totalHours: Double = 0.0,
                   @BeanProperty
                   taskTypes: Map[TypeName, Double] = Map(),
                   @BeanProperty
                   projectsMap: Map[ProjectName, Map[TypeName, Double]] = Map(),
                   @BeanProperty
                   valuesMap: Map[String, DayValue] = Map()
                 )

object Types {

  trait Status
  type StatusName = String @@ Status

  trait Type
  type TypeName = String @@ Status

  trait Project
  type ProjectName = String @@ Project

}


