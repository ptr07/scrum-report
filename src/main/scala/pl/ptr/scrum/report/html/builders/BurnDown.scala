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

import scala.collection.immutable.Map

private[html] class BurnDown(report: Report) extends Builder(report) {

  def build: Map[String, Object] = {
    Map(
      "labels" -> makeListOfString(labels),
      "values" -> makeListOfValues(values),
      "idealValues" -> makeListOfValues(idealValues),
      "trendValues" -> makeListOfValues(trendValues)
    )
  }


  private val values: List[Double] = {
    def getDoneValue(map: Map[String, Double]): Double = {
      val total = report.totalHours
      if (map.contains(conf.doneStatus)) {
        total - map(conf.doneStatus)
      } else {
        total
      }
    }

    labels.map(label => report.valuesMap.get(label)).filter(_.isDefined).map(_.get.statusHours).map(getDoneValue)
  }

  private val idealValues: List[Double] = {
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

  private val trendValues: List[Double] = {
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
