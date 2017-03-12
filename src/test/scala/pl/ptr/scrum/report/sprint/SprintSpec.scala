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

import java.io.{BufferedInputStream, File, FileInputStream}
import java.time.LocalDate

import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import pl.ptr.scrum.report.dto.{DayValue, Report}
import pl.ptr.scrum.report.utils.Implicits._

/**
  * Created by ptr on 11.02.17.
  */
class SprintSpec extends FlatSpec with Matchers {


  "A Sprint" should "be started" in {
    val sprint = new Sprint(15, "Szopy")
    val createdDto = sprint.startSprint(LocalDate.parse("2017-01-30"),
      LocalDate.parse("2017-02-10"), Map("Bug"->10.0,"Story"->15.5,"Change Request"->1.02),
      Map("CAN"->Map("Bug"->10.0,"Story"->15.5,"Change Request"->1.02))
    )
    createdDto should be equals (sprint.readSprint)
  }

  "it" should "must write and read data" in {
    val sprint = new Sprint(15, "Szopy")
    val dto = new Report(15, LocalDate.parse("2017-01-30"), LocalDate.parse("2017-02-10"), "Szopy", 120,
      Map("Bug"->10.0,"Story"->15.5,"Change Request"->1.02),
      Map("CAN"->Map("Bug"->10.0,"Story"->15.5,"Change Request"->1.02)),
      Map("30/01" -> DayValue(Map("Done" -> 120), Map()),
        "31/01" -> DayValue(Map("Done" -> 118), Map()),
        "01/02" -> DayValue(Map("Done" -> 102), Map()), "02/02" -> DayValue(Map("Done" -> 84), Map())
        , "03/02" -> DayValue(Map("Done" -> 87), Map()), "06/02" -> DayValue(Map("Done" -> 45), Map())))
    sprint.writeSprint(dto)
    dto should be equals (sprint.readSprint())
  }


}
