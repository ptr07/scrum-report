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

import java.io.{BufferedInputStream, File, FileInputStream}
import pl.ptr.scrum.report.utils.TypeMagic._
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

/**
  * Created by ptr on 11.02.17.
  */
class ParserSpec extends FlatSpec with Matchers with BeforeAndAfterEach {

  "An example xls file" should "be extracted and have valid size" in {
    val list = new Parser().parseData(exampleFis)
    list.size should be(119)
  }


  "Bad file" should "be recognized" in {
    val badFis = new BufferedInputStream(new FileInputStream(new File("./src/test/resources/notxls.xls")))
    val list = new Parser().parseData(badFis)
    list should be(empty)
  }

  "A example xls file" should "have valid data in status" in {
    val list = new Parser().parseData(exampleFis)
    val ga = new GroupingAlgorithm

    val map = ga.groupAndCountByStatus(list)
    map.get("Blocked".statusName) should be(Some(6))
    map.get("Business done".statusName) should be(Some(34.5))
    map.get("Developed".statusName) should be(Some(4))
    map.get("Done".statusName) should be(Some(0.5))
    map.get("In Progress".statusName) should be(Some(14.5))
    map.get("In Test".statusName) should be(Some(36.5))
    map.get("Reopened".statusName) should be(Some(2))
    map.get("To Do".statusName) should be(Some(45.5))
  }

  "A example xls file" should "have valid data in task types" in {
    val list = new Parser().parseData(exampleFis)
    val ga = new GroupingAlgorithm

    val map = ga.groupAndCountHoursByType(list)
    map.get("Story".typeName) should be(Some(126))
    map.get("Change request".typeName) should be(Some(1.0))
    map.get("Bug".typeName) should be(Some(16.5))

  }

  "A example xls file" should "have valid data in task projects" in {
    val list = new Parser().parseData(exampleFis)
    val ga = new GroupingAlgorithm

    val map = ga.groupAndCountHoursByProjectAndType(list)
    map should contain key ("ANT".projectName)
    map should contain key ("CDB".projectName)
    map should contain key ("KOR".projectName)
    map should contain key ("SOF".projectName)
    map should contain key ("AB".projectName)

    val antMap = map.get("ANT".projectName)

    antMap.get("Story".typeName) should be(59.0)
    antMap.get("Change request".typeName) should be(1.0)
    antMap.get("Bug".typeName) should be(6.5)



  }



  var exampleFis: BufferedInputStream = null


  override def beforeEach() {
    exampleFis = new BufferedInputStream(new FileInputStream(new File("./src/test/resources/report.xls")))
  }

  override def afterEach() {
    exampleFis.close()
  }
}
