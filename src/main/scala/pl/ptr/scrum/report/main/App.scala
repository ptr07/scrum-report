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
package pl.ptr.scrum.report.main

import java.io.{BufferedInputStream, File, FileInputStream}
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.apache.commons.io.FileUtils
import pl.ptr.scrum.report.data.{GroupingAlgorithm, Parser}
import pl.ptr.scrum.report.dto.DayValue
import pl.ptr.scrum.report.html.Html
import pl.ptr.scrum.report.sprint.Sprint
import resource._
import scopt._

case class Config(
                   command: String = "",
                   file: Option[File] = None,
                   out: Option[File] = None,
                   sprintNumber: Int = -1,
                   dateFrom: LocalDate = LocalDate.now(),
                   dateTo: LocalDate = LocalDate.now(),
                   date: LocalDate = LocalDate.now(),
                   teams: Seq[String] = Seq.empty
                 )


object Main extends App {

  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")
  val groupingAlgorithm = new GroupingAlgorithm()
  val xlsParser = new Parser()


  val parser = new OptionParser[Config]("scrum-report") {
    head("scrum-report", "0.1")

    opt[Int]('n', "sprintNumber").required().action((x, c) =>
      c.copy(sprintNumber = x)).text("Enter sprint number")

    cmd("start").action((_, c) => c.copy(command = "start")).
      text("Start sprint").
      children(
        opt[File]('f', "file").required().action((x, c) =>
          c.copy(file = Some(x))).text("Sprint report file"),
        opt[String]('s', "dateFrom").required().action((x, c) =>
          c.copy(dateFrom = LocalDate.parse(x))).text("Sprint start date"),
        opt[String]('e', "dateTo").required().action((x, c) =>
          c.copy(dateTo = LocalDate.parse(x))).text("Sprint end date")

      )

    cmd("data").action((_, c) => c.copy(command = "data")).
      text("Add date from xls report").
      children(
        opt[File]('f', "file").required().action((x, c) =>
          c.copy(file = Some(x))).text("Sprint report file"),
        opt[String]('d', "date").optional().action((x, c) =>
          c.copy(date = LocalDate.parse(x))).text("Report date"),
        opt[Unit]('y', "yesterday").optional().action((_, c) =>
          c.copy(date = LocalDate.now().minusDays(1))).text("Report date is yesterday")
      )

    cmd("report").action((_, c) => c.copy(command = "report")).
      text("Generate html report").
      children(
        opt[File]('o', "out").action((x, c) =>
          c.copy(out = Some(x))).text("Output file")
      )

    arg[String]("<team1> <team2> ...").unbounded().required().action((x, c) =>
      c.copy(teams = c.teams :+ x)).text("Teams")

    help("help").text("prints this usage text")
  }


  parser.parse(args, Config()) match {
    case Some(config) => {
      config.command match {
        case "start" => config.teams.foreach(team => {
          if (config.file.isDefined && config.file.get.exists()) {
            for {
              fis <- managed(new BufferedInputStream(new FileInputStream(config.file.get)))
            } {
              val list = xlsParser.parseData(fis)
              val taskTypes = groupingAlgorithm.groupAndCountHoursByType(list)
              val projectMap = groupingAlgorithm.groupAndCountHoursByProjectAndType(list)
              new Sprint(config.sprintNumber, team)
                .startSprint(config.dateFrom, config.dateTo, taskTypes, projectMap)
            }
          } else {
            println("File not extist!")
          }
        })

        case "data" => {
          if (config.file.isDefined && config.file.get.exists()) {
            config.teams.foreach(team => {
              val sprint = new Sprint(config.sprintNumber, team)
              val dto = sprint.readSprint()
              for {
                fis <- managed(new BufferedInputStream(new FileInputStream(config.file.get)))
              } {
                val list = xlsParser.parseData(fis)
                val time = groupingAlgorithm.groupAndCountByStatus(list)
                val projectMap = groupingAlgorithm.groupAndCountHoursByProjectAndType(groupingAlgorithm.filterDone(list))
                val workLogMap = groupingAlgorithm.groupAndCountWorkByProjectAndType(list)

                val doneHoursByType = groupingAlgorithm.groupAndCountHoursByType(groupingAlgorithm.filterDone(list))
                val workLogByType = groupingAlgorithm.groupAndCountWorkByType(list)

                val map = (formatter.format(config.date) -> DayValue(time, doneHoursByType, workLogByType, projectMap, workLogMap))
                sprint.writeSprint(dto.copy(valuesMap = dto.valuesMap + map))
              }
            })
          } else {
            println("File not extist!")
          }

        }
        case "report" => {
          if (config.out.isDefined) {
            val html = new Html(config.sprintNumber, config.teams.map(new Sprint(config.sprintNumber, _).readSprint()).toList)
            FileUtils.write(config.out.get, html.getHtml, Charset.defaultCharset)
          }
        }
        case _ => {
          println("No command selected")
        }
      }
    }

    case None =>
      println("Fatal error")
  }

}
