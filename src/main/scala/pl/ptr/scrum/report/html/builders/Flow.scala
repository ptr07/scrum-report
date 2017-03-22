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

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.collection.immutable.Map


private[html] class Flow(report: Report) extends Builder(report) {
  private val statusValues: List[StatusValues] = {
    conf.statuses.map(status => {
      val name = status.name
      val color = status.color
      val values = labels.map(label => report.valuesMap.get(label)).map(_.getOrElse(DayValue()).hoursByStatus)
        .map(m => m.getOrElse(name, 0.0))
      StatusValues(name, color, values)
    })
  }

  def build: Map[String, Object] = {
    Map(
      "labels" -> makeListOfString(labels),
      "statusValues" -> statusValues.asJava
    )
  }
}

private[html] case class StatusValues(
                                       @BeanProperty name: StatusName,
                                       @BeanProperty color: String,
                                       @BeanProperty values: List[Double]) {
  def valuesString: String = values.mkString(",")

}
