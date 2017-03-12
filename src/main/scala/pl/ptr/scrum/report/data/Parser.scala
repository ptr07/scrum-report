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

import java.io.BufferedInputStream

import org.apache.commons.lang3.StringUtils
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.{Cell, DataFormatter, Row}
import pl.ptr.scrum.report.dto.Types.StatusName
import pl.ptr.scrum.report.utils.ConfigurationLoader
import  pl.ptr.scrum.report.utils.TypeMagic._

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.collection.immutable.Map

/**
  * JIRA task representation
  *
  * @param kind      task type
  * @param summary   task name
  * @param project   task project
  * @param status    task status
  * @param estimate  task estimation in seconds
  * @param timeSpent work time logged for task in seconds
  */
case class Task(kind: String, summary: String, project: String, status: StatusName, estimate: Long, timeSpent: Long){
  private val Conf = ConfigurationLoader.config

  def isFinished: Boolean ={
    Conf.doneStatus == status
  }

  def isBuffer: Boolean ={
    !isFinished && summary.toLowerCase.contains(Conf.bufferSummary)
  }

}

/**
  *
  * Delivers methods for xls data parsing
  *
  *
  */
class Parser() {

  /**
    * Read data from xls in InputStream. It validates and parse file.
    * @param from xls file input stream
    * @return
    */
   def parseData(from: BufferedInputStream): List[Task] = {

    val Header = List("Issue Type",
      "Key", "Summary", "Assignee", "Reporter", "Priority", "Status",
      "Resolution", "Created", "Updated", "Due Date",
      "Original Estimate", "Time Spent")

    /**
      * Checks if report has valid header. Valid header should have values that are strictly defined.
      *
      * @param row row with report header
      * @return true if header matches
      */
    def validHeader(row: Row): Boolean = {

      def preprocess(cell: Cell): Option[String] =
        if (StringUtils.isNoneEmpty(cell.getStringCellValue)) {
          Some(cell.getStringCellValue.replaceAll("\n", "").replaceAll(" +", " "))
        } else {
          None
        }

      row.cellIterator().asScala.map(preprocess).zipWithIndex.forall {
        case (cell: Option[String], i: Int) =>
          cell.isDefined && cell.get.equals(Header(i))
      }
    }

    def parseRow(row: Row): Option[Task] = {
      def isAllDigits(x: String) = x forall Character.isDigit

      def getLong(cellValue: String) = if (StringUtils.isNoneEmpty(cellValue) && isAllDigits(cellValue)) cellValue.toLong else 0L

      def getProject(cellValue: String) = {
        val regex = "([a-zA-Z]+)-[0-9]+"r
        val result = for (m <- regex.findFirstMatchIn(cellValue)) yield m.group(1)
        result.getOrElse("")
      }

      val df = new DataFormatter()
      val cellList = row.cellIterator.asScala.toList.map(df.formatCellValue)
      val cellMap = (Header zip cellList) (breakOut): Map[String, String]

      if (cellList.size == Header.length && StringUtils.isNoneEmpty(cellMap("Summary"))) {
        Some(
          Task(
            cellMap("Issue Type"),
            cellMap("Summary"),
            getProject(cellMap("Key")),
            cellMap("Status").statusName,
            getLong(cellMap("Original Estimate")),
            getLong(cellMap("Time Spent"))
          )
        )
      } else {
        None
      }
    }

    def extractData(rows: List[Row]): List[Task] = {
      rows.map(row => parseRow(row)).filter(_.isDefined).map(_.get)
    }

    def getWorkBook(from: BufferedInputStream): HSSFWorkbook = {
      if (POIFSFileSystem.hasPOIFSHeader(from)) {
        new HSSFWorkbook(from)
      } else {
        new HSSFWorkbook()
      }
    }

    def getSheet(wb: HSSFWorkbook): Option[HSSFSheet] = {
      if (wb.getNumberOfSheets > 0) {
        Some(wb.getSheetAt(0))
      } else {
       None
      }
    }

    val sheet = getSheet(getWorkBook(from))
    if (sheet.isDefined && sheet.get.getLastRowNum > 2 && validHeader(sheet.get.getRow(3))) {
      extractData(sheet.get.rowIterator().asScala.toList.drop(DataNumber))
    } else {
      List()
    }


  }

  /**
    * Where to start with the data in the file? The fourth row contains ticket information.
    */
  private val DataNumber = 4


}


