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
  * Created by ptr on 12.03.17.
  */
class BurnDownSpec extends FlatSpec with Matchers {

  val trivialReport = Report(15, LocalDate.parse("2017-01-30"), LocalDate.parse("2017-02-10"),
    "test",
    130,
    Map("Bug".typeName -> 120, "Story".typeName -> 10),
    Map("CM".projectName -> Map("Bug".typeName -> 120, "Story".typeName -> 10)),
    Map()
  )

  behavior of "An example BurnDown chart generator"

  "BurnDown for empty value map " should "generate empty result without errors" in {
    val burnDown = new BurnDown(trivialReport)

    burnDown.values.length should be(0)
    burnDown.idealValues.length should be (10)
    burnDown.trendValues.length should be (10)
  }

  val singleReport = trivialReport.copy(valuesMap =   Map("30/01" -> DayValue(Map("To Do".statusName -> 130), Map())))

  "it" should "generate single value for single valueMap" in {
    val burnDown = new BurnDown(singleReport)

    burnDown.values.length should be(1)
    burnDown.values.head should be (130)
  }


  val withEmptyReport =
    trivialReport.copy(valuesMap =
      Map("30/01" -> DayValue(Map("To Do".statusName -> 130), Map()),
        "31/01" -> DayValue(Map("To Do".statusName -> 120,"Business done".statusName -> 10), Map()),
        "02/02" -> DayValue(Map("To Do".statusName -> 100,"Business done".statusName -> 30), Map())

      ))

  "it" should "fill holes in data using previous value" in {
    val burnDown = new BurnDown(withEmptyReport)

    burnDown.values.length should be(4)
    burnDown.values(0) should be (130)
    burnDown.values(1) should be (120)
    burnDown.values(2) should be (120)
    burnDown.values(3) should be (100)
  }

  behavior of "An IdealLine"


  "Ideal line for value map " should "generate result without errors" in {
    val burnDown = new BurnDown(trivialReport)

    burnDown.idealValues.length should be(10)
    burnDown.idealValues(0) should be (130)
    burnDown.idealValues(9) should be (0)

  }


  behavior of "An TrendLine"


  "Trend line for value map " should "generate result without errors" in {
    val burnDown = new BurnDown(withEmptyReport)

    burnDown.trendValues.length should be(10)
    burnDown.trendValues(0) should be (130)
    burnDown.trendValues(9) should be (40)

  }


}
