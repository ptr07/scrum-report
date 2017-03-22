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
package pl.ptr.scrum.report.html.builders

import pl.ptr.scrum.report.dto.Types.TypeName
import pl.ptr.scrum.report.dto.{DayValue, Report}

import scala.collection.immutable.Map

/**
  * Created by ptr on 02.03.17.
  */
private[html] class Round(report: Report) extends Builder(report) {

  private val taskTypesNames: List[TypeName] = conf.types.map(_.name)
  private val taskTypesColors: List[String] = conf.types.map(_.color)
  private val taskTypesValues: List[Double] = conf.types.map(tt => report.taskTypes.getOrElse(tt.name, 0.0))
  private val workLogByType: List[Double] = hours(_.workLogByType)
  private val doneHoursByType: List[Double] = hours(_.doneHoursByType)

  def build: Map[String, Object] = {
    Map(
      "taskTypesNames" -> makeListOfString(taskTypesNames),
      "taskTypesValues" -> makeListOfValues(taskTypesValues),
      "taskTypesColors" -> makeListOfString(taskTypesColors),

      "workLogByType" -> makeListOfValues(workLogByType),
      "doneHoursByType" -> makeListOfValues(doneHoursByType)
    )

  }

  private def hours(valueFunc: DayValue => Map[TypeName, Double]): List[Double] = {
    val lastLabel = labels.reverse.find(report.valuesMap.contains)

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {
      val valuesInLastDay = valueFunc(report.valuesMap(lastLabel.get))
      conf.types.map(t => valuesInLastDay.get(t.name)).map(op => if (op.isDefined) op.get else 0.0)
    } else {
      List()
    }

  }

}