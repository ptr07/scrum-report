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
package pl.ptr.scrum.report.utils

import java.time.{LocalDate, ZoneId}
import java.util.Date

import com.typesafe.config.ConfigFactory
import pl.ptr.scrum.report.dto.Types.{StatusName, TypeName}
import pureconfig._
import pl.ptr.scrum.report.utils.TypeMagic._


/**
  * JIRA state with assigned color
  *
  * @param name  task state name
  * @param color state color in HEX value, for example #b3b3cc
  */
case class StatusWithColor(name: StatusName, color: String)

/**
  * JIRA Issue type with assigned color
  *
  * @param name  task type name
  * @param color state color in HEX value, for example #b3b3cc
  */
case class TaskTypeWithColor(name: TypeName, color: String)

/**
  * Report configuration loaded from application.conf
  *
  * @param doneStatus    finished task status, used for burn down chart generation
  * @param toDoStatus    initial task state
  * @param bufferSummary name of tasks used as buffers
  * @param status        list of available task statuses
  * @param color         color assigned to task status, or task type
  * @param taskTypes   used ticket types
  */
case class ReportConfig(doneStatus: String = "",
                        toDoStatus: String = "",
                        bufferSummary: String = "",
                        status: List[String] = List(),
                        color: Map[String, String] = Map(),
                        taskTypes: List[String] = List()
                       ) {
  /**
    * Creates list of available task status with colors in specified order
    *
    * @return list of all available task status in display order
    */
  def statuses: List[StatusWithColor] = status.map(name => StatusWithColor(name.statusName, color.getOrElse(name, "#b3b3cc")))

  def types: List[TaskTypeWithColor] = taskTypes.map(name => TaskTypeWithColor(name.typeName, color.getOrElse(name, "#b3b3cc")))

  def doneStatusName : StatusName = doneStatus.statusName

}

/**
  *  [[ReportConfig]] factory
  */
object ConfigurationLoader {

  /**
    * Loads [[ReportConfig]] from application.conf
    *
    * @return valid config or default one, when parsing failed
    */
  def config: ReportConfig = {
    val confTry = loadConfig[ReportConfig](ConfigFactory.load())
    confTry.getOrElse(ReportConfig())
  }
}

/**
  * Implicit conversions and helpers for [[java.time.LocalDate]] and [[java.util.Date]] instances.
  *
  */
object Implicits {
  implicit def localDateToDate(date: LocalDate): Date = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())

  implicit def dateToLocalDate(date: Date): LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

}

object TypeMagic {
  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]

  implicit class TaggedString(val s: String) extends AnyVal {
    def statusName : StatusName = s.asInstanceOf[StatusName]

    def typeName : TypeName = s.asInstanceOf[TypeName]

  }
}
