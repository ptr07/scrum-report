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
package pl.ptr.scrum.report.data

import pl.ptr.scrum.report.dto.Types.{Project, ProjectName, StatusName, TypeName}
import pl.ptr.scrum.report.utils.ConfigurationLoader

import scala.collection.immutable.Map

/**
  * Delivers methods for task clustering
  */
class GroupingAlgorithm {

  /**
    * Tickets are grouped by status. Time logged in 'Buffers' is subtracted from initial status
    * and is added to finish ticket status. Exceeded buffers is not accepted.
    *
    * @param tasks list of task
    * @return number of hours logged by ticket status
    */
  def groupAndCountByStatus(tasks: List[Task]): Map[StatusName, Double] = {
    countBuffersTasks(tasks).groupBy(_.status).map(kv => (kv._1, count(estimate)(kv._2)))

  }

  /**
    * Tickets are grouped by issue type.
    *
    * @param tasks list of task
    * @return number of hours estimated by ticket type
    */
  def groupAndCountHoursByType(tasks: List[Task]): Map[TypeName, Double] = {
    countBuffersTasks(tasks).groupBy(task => task.kind).map(kv => (kv._1, count(estimate)(kv._2)))
  }

  /**
    * Tickets are grouped by issue type.
    *
    * @param tasks list of task
    * @return number of hours logged by ticket type
    */
  def groupAndCountWorkByType(tasks: List[Task]): Map[TypeName, Double] = {
    countBuffersTasks(tasks).groupBy(task => task.kind).map(kv => (kv._1, count(workLog)(kv._2)))
  }


  /**
    * Tickets are grouped by project and issue type.
    *
    * @param tasks list of task
    * @return number of hours estimated by ticket type
    */
  def groupAndCountHoursByProjectAndType(tasks: List[Task]): Map[ProjectName, Map[TypeName, Double]] = {
    def countMap(taskByKind: Map[TypeName, List[Task]]) = taskByKind.map(kv => (kv._1, count(estimate)(kv._2)))

    countBuffersTasks(tasks).groupBy(task => task.project).map(kv => (kv._1, countMap(kv._2.groupBy(_.kind))))
  }

  /**
    * Tickets are grouped by project and issue type.
    *
    * @param tasks list of task
    * @return number of hours logged by ticket type
    */
  def groupAndCountWorkByProjectAndType(tasks: List[Task]): Map[ProjectName, Map[TypeName, Double]] = {
    def countMap(taskByKind: Map[TypeName, List[Task]]) = taskByKind.map(kv => (kv._1, count(workLog)(kv._2)))

    countBuffersTasks(tasks).groupBy(task => task.project).map(kv => (kv._1, countMap(kv._2.groupBy(_.kind))))
  }

  /**
    * Filter tasks that are finished
    * @param tasks all task from report
    * @return
    */
  def filterDone(tasks: List[Task]) : List[Task] = {
    countBuffersTasks(tasks).filter(_.isFinished)
  }


  private def countBuffersTasks(tasks: List[Task]): List[Task] = {

    def splitTask(task: Task): List[Task] = {
      val tEstimate = task.estimate
      val tTimeSpend = if (task.timeSpent > tEstimate) tEstimate else task.timeSpent
      List(task.copy(estimate = tEstimate - tTimeSpend), task.copy(status=Conf.doneStatusName,estimate = tTimeSpend))
    }

    tasks.flatMap(task => if (task.isBuffer) {
      splitTask(task)
    } else {
      List(task)
    })
  }

  private def estimate(task: Task): Long = task.estimate

  private def workLog(task: Task): Long = task.timeSpent


  private def count(valFun: Task => Long)(tasks: List[Task]): Double = Math.round(tasks.map(valFun).sum * 100 / SecondsInHour) / 100.0


  private val SecondsInHour = 3600.0
  private val Conf = ConfigurationLoader.config


}
