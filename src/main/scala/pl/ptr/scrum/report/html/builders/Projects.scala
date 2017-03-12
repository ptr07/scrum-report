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

import scala.beans.BeanProperty
import scala.collection.JavaConverters._


private[html] case class Project(@BeanProperty kind: String,
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
      "doneProjectsValues" -> (doneProjectsValues++toDoProjectsValues).asJava
    )

  }

  private val projectsNames: List[String] = {
    report.projectsMap.keys.toList.sorted
  }

  private val doneProjectsValues: List[Project] = {

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {
      conf.types.map(taskType => {
        val name = taskType.name
        val color = taskType.color
        val values = projectsNames.map(name => report.valuesMap(lastLabel.get)
          .projectsMap.getOrElse(name, Map())).map(_.getOrElse(name, 0.0))
        Project(name, color, values)
      })
    } else {
      List()
    }
  }

  private val toDoProjectsValues: List[Project] = {

    if (lastLabel.isDefined && report.valuesMap.contains(lastLabel.get)) {

      def getAllValueByName(name : String)  = report.projectsMap.getOrElse(name, Map()).values.sum
      def getDoneValueByName(name : String)  = report.valuesMap(lastLabel.get).projectsMap.getOrElse(name, Map()).values.sum

      val toDo = conf.toDoStatus
      val toDoColor = conf.color.getOrElse(toDo,"#b3b3cc")

      val values = projectsNames.map(name => getAllValueByName(name) - getDoneValueByName(name) )

      List(Project(toDo,toDoColor,values))
    }else{
      List()
    }
  }

  private val projectsValues: List[Project] = {
    conf.types.map(taskType => {
      val name = taskType.name
      val color = taskType.color
      val values = projectsNames.map(name => report.projectsMap.getOrElse(name, Map())).map(_.getOrElse(name, 0.0))
      Project(name, color, values)
    })
  }
}