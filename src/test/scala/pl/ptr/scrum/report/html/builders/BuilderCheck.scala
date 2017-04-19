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

import java.time.{LocalDate, ZoneId}
import java.util.Date

import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Arbitrary, Gen, Properties}
import pl.ptr.scrum.report.dto.Report

object ArbitraryValues {
  implicit val abc: Arbitrary[LocalDate] = Arbitrary(Gen.choose(1220227200L * 1000, new Date().getTime).map(
    new Date(_).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
  ))
}

/**
  * Created by ptr on 19.04.17.
  */
object BuilderCheck extends Properties("Builder") {

  import ArbitraryValues._

  private val builder = new MockBuilder()

  val propNotEmpty = forAll { (dateFrom: LocalDate, dateTo: LocalDate) =>
    (dateFrom.isBefore(dateTo)) ==> (builder.datesFromTo(dateFrom, dateTo).length > 0)
  }


  property("startsWith") = propNotEmpty

  private class MockBuilder() extends Builder(new Report()) {
    def build(): Map[String, Object] = Map()

  }

}
