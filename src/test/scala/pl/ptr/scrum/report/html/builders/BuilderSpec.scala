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

import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}
import pl.ptr.scrum.report.dto.{DayValue, Report}
import pl.ptr.scrum.report.utils.Implicits._
import pl.ptr.scrum.report.utils.TypeMagic._

/**
  * Created by ptr on 19.03.17.
  */
class BuilderSpec extends FlatSpec with Matchers {

  behavior of "An Builder chart generator"

  val trivialReport = Report(15, LocalDate.parse("2017-01-30"), LocalDate.parse("2017-02-10"),
    "test",
    130,
    Map("Bug".typeName -> 120, "Story".typeName -> 10),
    Map("CM".projectName -> Map("Bug".typeName -> 120, "Story".typeName -> 10)),
    Map()
  )


  "Label generated for report" should " ignore weekends and count every other day" in {
    val builder = new MockBuilder(trivialReport)

    builder.publicLabels should have size (10)
    builder.publicLabels(0) should be("30/01")
    builder.publicLabels(9) should be("10/02")

  }

  "LastLabel generated for report" should " be last date used in value maps" in {
    val trivialBuilder = new MockBuilder(trivialReport)

    trivialBuilder.publicLastLabel should not be defined

    val builder = new MockBuilder(report)

    builder.publicLastLabel should be(Some("06/02"))

  }

  val holiday = Report(15, LocalDate.parse("2017-05-01"), LocalDate.parse("2017-05-10"),
    "test",
    130,
    Map(),
    Map(),
    Map()
  )

  "Label generated for holiday report" should " ignore weekends and holidays and count every other day" in {
    val builder = new MockBuilder(holiday)

    builder.publicLabels should have size (6)
    builder.publicLabels(0) should be("02/05")
    builder.publicLabels(5) should be("10/05")

  }

  val report = new Report(15, LocalDate.parse("2017-01-30"), LocalDate.parse("2017-02-10"),
    "test",
    120,
    Map("Bug".typeName -> 120, "Story".typeName -> 10),
    Map("CM".projectName -> Map("Bug".typeName -> 120, "Story".typeName -> 10)),
    Map("30/01" -> DayValue(Map("Done".statusName -> 120), Map()),
      "31/01" -> DayValue(Map("Done".statusName -> 118), Map()),
      "01/02" -> DayValue(Map("Done".statusName -> 102), Map()), "02/02" -> DayValue(Map("Done".statusName -> 84), Map())
      , "03/02" -> DayValue(Map("Done".statusName -> 87), Map()), "06/02" -> DayValue(Map("Done".statusName -> 45), Map()))
  )

  private class MockBuilder(report: Report) extends Builder(report) {
    def build(): Map[String, Object] = Map()

    def publicLabels: List[String] = labels

    def publicLastLabel: Option[String] = lastLabel
  }


}
