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
  * Simple Burndown chart. For each day in Sprint, it subtract number of done hours from total hours.
  * It presents team velocity.
  * Trend line is generated to predict future velocity.
  * Ideal line is generated to show appropriate ticket burning.
  *
  * @param report
  */
private[html] class BurnDown(report: Report) extends Builder(report) {

  def build: Map[String, Object] = {
    Map(
      "labels" -> makeListOfString(labels),
      "values" -> makeListOfValues(values),
      "idealValues" -> makeListOfValues(idealValues),
      "trendValues" -> makeListOfValues(trendValues)
    )
  }


  /**
    * For each day in Sprint, it subtract number of done hours from total hours.
    * Holes in data is filled by previous values.
    */
  private[html] val values: List[Double] = {

    /**
      * Calculates result
      *
      * @param map number of hours for task in status
      * @return total number of hours subtract done hours
      */
    def getDoneValue(map: Map[StatusName, Double]): Double = {
      val total = report.totalHours
      if (map.contains(conf.doneStatusName)) {
        total - map(conf.doneStatusName)
      } else {
        total
      }
    }

    /**
      * Checks if it is last label defined in valuesMap as key
      *
      * @param label to analyze
      * @return
      */
    def isNotLastValue(label: String): Boolean =
      labels.drop(labels.indexOf(label)).find(report.valuesMap.contains).isDefined


    /**
      * Searches for values defined for previous days
      *
      * @param label to analyze
      * @return previoud DayValue
      */
    def getLastDefinedValue(label: String): DayValue = {
      val prevValue = labels.take(labels.indexOf(label)).reverse.find(report.valuesMap.contains)
      report.valuesMap.getOrElse(prevValue.get, DayValue())

    }

    if (report.valuesMap.isEmpty) {
      List()
    } else {
      labels
        .filter(isNotLastValue).map(label => (label, report.valuesMap.get(label)))
        .map { case (label, option) => option.getOrElse(
          getLastDefinedValue(label)).hoursByStatus
        }
        .map(getDoneValue)
    }

  }


  /**
    * Ideal line - line connecting beginning of sprint (total hours) with sprint end (0 hours)
    */
  private[html] val idealValues: List[Double] = {
    val xx = labelsRange
    if (xx.isEmpty) {
      List()
    } else {
      def y(x: Double): Double = (-report.totalHours / xx.last * x + report.totalHours).toInt

      xx.map(x => y(x))
    }
  }

  private val trendFunction: (Double) => Double = { (x: Double) => {
    val valuesWithIndex = values.zipWithIndex
    val cutted = if (valuesWithIndex.length > 3 && valuesWithIndex.length < labels.size) {
      valuesWithIndex.takeRight(3)
    } else {
      valuesWithIndex
    }
    val yy = cutted.map(_._1.toDouble)
    val xx = cutted.map(_._2.toDouble)

    if (xx.isEmpty || yy.isEmpty) {
      0.0
    } else {
      getFunction(xx.head, yy.head, xx.last, yy.last)(x)
    }

  }
  }

  /**
    * Simple trend line based on values from last three days.
    */
  private[html] val trendValues: List[Double] = {
    val xx = labelsRange
    if (xx.isEmpty) {
      List()
    } else {
      xx.map(x => trendFunction(x))
    }
  }


  private def getFunction(x1: Double, y1: Double, x2: Double, y2: Double): (Double) => Double = (x: Double) => {
    (y1 + ((y2 - y1) / (x2 - x1)) * (x - x1)).toInt
  }


}
