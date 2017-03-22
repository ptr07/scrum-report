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
package pl.ptr.scrum.report.html

import java.io.StringWriter
import java.time.format.DateTimeFormatter

import freemarker.template.Configuration
import pl.ptr.scrum.report.dto.Report
import pl.ptr.scrum.report.html.builders._
import pl.ptr.scrum.report.utils.Implicits._

import scala.collection.JavaConverters._

/**
  * Generated html report using list of [[Report]] data and freemarker templates
  *
  * @param sprintNumber number of sprint
  * @param reports      list of [[Report]] data
  */
class Html(sprintNumber: Int, reports: List[Report]) {

  val freemarkerConf = new Configuration
  freemarkerConf.setClassForTemplateLoading(getClass, "templates")
  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

  def getHtml: String = {

    val template = freemarkerConf.getTemplate("template.ftl")
    val data = scala.collection.mutable.Map[String, Object]()
    data.put("sprintNumber", sprintNumber.toString)
    data.put("charts", reports.map(getChartHtml).asJava)
    val sw = new StringWriter()
    template.process(data.asJava, sw)
    sw.toString
  }

  private def getChartHtml(dto: Report): String = {

    val template = freemarkerConf.getTemplate("chart.ftl")
    val data = scala.collection.mutable.Map[String, Object]()
    data.put("sprintNumber", sprintNumber.toString)
    data.put("dateFrom", formatter.format(dto.dateFrom))
    data.put("dateTo", formatter.format(dto.dateTo))
    data ++= new BurnDown(dto).build
    data ++= new Flow(dto).build
    data ++= new Pie(dto).build
    data ++= new Round(dto).build
    data ++= new Projects(dto).build

    data.put("name", dto.team)
    val sw = new StringWriter()
    template.process(data.asJava, sw)
    sw.toString

  }


}
