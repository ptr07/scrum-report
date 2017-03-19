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

import pl.ptr.scrum.report.dto.Types.{ProjectName, TypeName}
import pl.ptr.scrum.report.dto.{DayValue, Report}
import pl.ptr.scrum.report.utils.TypeMagic._

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.collection.immutable.Map


private[html] case class Project(@BeanProperty kind: TypeName,
                                 @BeanProperty color: String,
                                 @BeanProperty values: List[Double]) {
  def valuesString: String = values.mkString(",")

}

/**
  * Created by ptr on 02.03.17.
  */
private[html] class Projects(report: Report) extends Builder(report) {

  def build: Map[String, Object] = {
    Map(
      "projectsNames" -> makeListOfString(projectsNames),
      "projectsValues" -> projectsValues.asJava,
      "doneProjectsValues" -> (doneProjectsValues ++ toDoProjectsValues).asJava,
      "workLogProjectsValues" -> workLogProjectsValues.asJava

    )

  }

  private val projectsNames: List[ProjectName] = {
    report.projectsMap.keys.toList.sortWith(_ < _)
  }


  private val doneProjectsValues: List[Project] = hours(_.projectsMap)

  private val workLogProjectsValues: List[Project] = hours(_.workLogMap)


  private def hours(valueFunc: DayValue => Map[ProjectName, Map[TypeName, Double]]): List[Project] = {

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {
      conf.types.map(taskType => {
        val name = taskType.name
        val color = taskType.color
        val values = projectsNames.map(name => {
          val dayValue = report.valuesMap(lastLabel.get)
          valueFunc(dayValue).getOrElse(name.projectName, Map())
        }).map(_.getOrElse(name, 0.0))
        Project(name, color, values)
      })
    } else {
      List()
    }
  }


  private val toDoProjectsValues: List[Project] = {

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {

      def getAllValueByName(name: String) = report.projectsMap.getOrElse(name.projectName, Map()).values.sum

      def getDoneValueByName(name: String) = report.valuesMap(lastLabel.get).projectsMap.getOrElse(name.projectName, Map()).values.sum

      val toDo = conf.toDoStatus.typeName
      val toDoColor = conf.color.getOrElse(toDo, "#b3b3cc")

      val values = projectsNames.map(name => getAllValueByName(name) - getDoneValueByName(name))

      List(Project(toDo, toDoColor, values))
    } else {
      List()
    }
  }

  private val projectsValues: List[Project] = {
    conf.types.map(taskType => {
      val name = taskType.name
      val color = taskType.color
      val values = projectsNames.map(name => report.projectsMap.getOrElse(name.projectName, Map())).map(_.getOrElse(name, 0.0))
      Project(name, color, values)
    })
  }
}