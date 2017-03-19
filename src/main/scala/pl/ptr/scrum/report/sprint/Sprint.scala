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
package pl.ptr.scrum.report.sprint

import java.io._
import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import pl.ptr.scrum.report.dto.Report
import pl.ptr.scrum.report.dto.Types.{ProjectName, TypeName}
import pl.ptr.scrum.report.utils.Implicits._

import scala.collection.immutable.Map

/**
  * Utility class responsible for reading and writing sprint data to files.
  * Each sprint is stored in ~/.scrum-report directory in individual file.
  *
  * For example: sprint 10 for team1 will be saved in: ~/.scrum-report/file-team1-sprint-10.db
  *
  * @param sprintNumber number of sprint
  * @param team         team name
  */
class Sprint(sprintNumber: Int, team: String) {

  /**
    * Object <-> Json conversion using jackson
    */
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.writerWithDefaultPrettyPrinter()


  /**
    * Created db file. Each sprint is stored in ~/.scrum-report directory in individual file.
    * For example: sprint 10 for team1 will be saved in: ~/.scrum-report/file-team1-sprint-10.db
    * @return path to sprint db file
    */
  private def createDbFile : String = {
    val path = System.getProperty("user.home") + "/.scrum-report"
    val dir = new File(path)
    if (!dir.exists()) {
      dir.mkdir()
    }
    path + s"/file-$team-sprint-$sprintNumber.db"

  }

  /**
    * Reads data from db file to [[Report]] dto
    * @return
    */
  def readSprint(): Report = {
    mapper.readValue(new File(createDbFile), classOf[Report])
  }

  /**
    * Data from each day should be stored in db file. Write method updates sprint data using [[Report]] dto.
    *
    * @param dto
    */
  def writeSprint(dto: Report): Unit = {
    mapper.writeValue(new File(createDbFile), dto)
  }


  /**
    * Special method used for initializing sprint
    *
    * @param dateFrom    start date
    * @param dateTo      end date
    * @param taskByTypes initial task types generated on Spring Planning
    * @param projectsMap initial project map generated on Sprint Planning
    * @return [[Report]] object
    */
  def startSprint(dateFrom: LocalDate, dateTo: LocalDate, taskByTypes: Map[TypeName, Double], projectsMap: Map[ProjectName, Map[TypeName, Double]]): Report = {
    val dto = Report(sprintNumber, dateFrom, dateTo, team, taskByTypes.values.sum, taskByTypes, projectsMap, Map())
    mapper.writeValue(new File(createDbFile), dto)
    dto
  }

}
