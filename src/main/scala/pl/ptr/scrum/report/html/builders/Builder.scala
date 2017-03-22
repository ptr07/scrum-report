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

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, LocalDate}

import pl.ptr.scrum.report.dto.Report
import pl.ptr.scrum.report.utils.Implicits._
import pl.ptr.scrum.report.utils.{ConfigurationLoader, ReportConfig}

/**
  * Created by ptr on 02.03.17.
  */
private[html] abstract class Builder(report: Report) {

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
  protected val conf: ReportConfig = ConfigurationLoader.config
  protected val labels: List[String] = datesFromTo(report.dateFrom, report.dateTo).map(formatter.format(_))
  protected val lastLabel: Option[String] = labels.reverse.find(report.valuesMap.contains)
  protected val labelsRange: List[Int] = List.range(0, labels.size)


  def build: Map[String, Object]

  protected def makeListOfString(list: List[Object]): String = list.map("\"" + _ + "\"").mkString(",")

  protected def makeListOfValues(list: List[Double]): String = list.mkString(",")


  private def datesFromTo(dateFrom: LocalDate, dateTo: LocalDate): List[LocalDate] = {
    def isWeekend(dateFrom: LocalDate) = dateFrom.getDayOfWeek == DayOfWeek.SATURDAY || dateFrom.getDayOfWeek == DayOfWeek.SUNDAY

    if (dateFrom.isAfter(dateTo)) {
      List()
    } else {
      if (isWeekend(dateFrom)) {
        datesFromTo(dateFrom.plusDays(1), dateTo)
      } else {
        List(dateFrom) ++ datesFromTo(dateFrom.plusDays(1), dateTo)
      }
    }
  }


}
