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

import pl.ptr.scrum.report.dto.Report
import pl.ptr.scrum.report.dto.Types.TypeName

/**
  * Created by ptr on 02.03.17.
  */
private[html] class All(report: Report) extends Builder(report) {

  def build: Map[String, Object] = {
    Map(
      "taskTypesNames" -> makeListOfString(taskTypesNames),
      "taskTypesValues" -> makeListOfValues(taskTypesValues),
      "taskTypesColors" -> makeListOfString(taskTypesColors)
    )

  }

  val taskTypesNames: List[TypeName] = conf.types.map(_.name)

  val taskTypesColors: List[String] = conf.types.map(_.color)

  val taskTypesValues: List[Double] = conf.types.map(tt => report.taskTypes.getOrElse(tt.name, 0.0))


}