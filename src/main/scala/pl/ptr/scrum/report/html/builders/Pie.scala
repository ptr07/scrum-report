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

import pl.ptr.scrum.report.dto.Types.StatusName
import pl.ptr.scrum.report.dto.{DayValue, Report}

import scala.collection.immutable.Map

/**
  * Created by ptr on 02.03.17.
  */
private[html] class Pie(report: Report) extends Builder(report) {

  val taskTypesColors: List[String] = conf.types.map(_.color)
  private val hoursByStatus: List[Double] = hours(_.hoursByStatus)

  def build: Map[String, Object] = {
    Map(
      "labels" -> makeListOfString(labels),

      "statuses" -> makeListOfString(conf.statuses.map(_.name)),

      "hoursByStatus" -> makeListOfValues(hoursByStatus)
    )

  }

  private def hours(valueFunc: DayValue => Map[StatusName, Double]): List[Double] = {
    val lastLabel = labels.reverse.find(report.valuesMap.contains)

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {
      val valuesInLastDay = valueFunc(report.valuesMap(lastLabel.get))
      conf.statuses.map(status => valuesInLastDay.get(status.name)).map(op => if (op.isDefined) op.get else 0.0)
    } else {
      List()
    }

  }

}