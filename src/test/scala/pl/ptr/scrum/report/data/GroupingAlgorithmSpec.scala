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

import org.scalatest.{FlatSpec, Matchers}
import pl.ptr.scrum.report.utils.TypeMagic._


class GroupingAlgorithmSpec extends FlatSpec with Matchers {

  behavior of "An example GroupingAlgorithm"
  val groupingAlgorithm = new GroupingAlgorithm

  "An example task list " should "be grouped by status and have valid number of hours" in {
    val tasks = List(Task("Bug".typeName, "1", "P1".projectName, "To Do".statusName, 3600, 2400), Task("Bug".typeName, "2", "P2".projectName, "To Do".statusName, 7200, 2400), Task("Bug".typeName, "3", "P1".projectName, "Closed".statusName, 8000, 3400))


    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do".statusName
    map.get("To Do".statusName) should be(Some(3))

  }

  "it" should "not round incomplete hours " in {
    val tasks = List(Task("Bug".typeName, "1", "P1".projectName, "To Do".statusName, 1700, 2400), Task("Bug".typeName, "1", "P1".projectName, "Developed".statusName, 1900, 2400))
    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do".statusName
    map.get("To Do".statusName) should be(Some(0.47))

    map should contain key "Developed".statusName
    map.get("Developed".statusName) should be(Some(0.53))

  }

  "it" should "subtract not finished buffer tasks from 'to do' and add it to 'done'" in {
    val tasks = List(Task("Bug".typeName, "bufor", "P1".projectName, "To Do".statusName, 3600, 3600), Task("Bug".typeName, "bufor", "P1".projectName, "Business done".statusName, 7200, 3600))

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do".statusName
    map.get("To Do".statusName) should be(Some(0))

    map should contain key "Business done".statusName
    map.get("Business done".statusName) should be(Some(3))

  }


  "it" should "not accepted exceeded buffers" in {
    val tasks = List(Task("Bug".typeName, "bufor", "P1".projectName, "To Do".statusName, 3600, 7200))

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do".statusName
    map.get("To Do".statusName) should be(Some(0))

    map should contain key "Business done".statusName
    map.get("Business done".statusName) should be(Some(1))

  }


  "it" should "return map with two empty values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (0)

  }

  "An example task list " should "be grouped by task type and have valid number of hours" in {
    val tasks = List(
      Task("Bug".typeName, "1", "P1".projectName, "To Do".statusName, 3600, 2400),
      Task("Bug".typeName, "2", "P2".projectName, "To Do".statusName, 7200, 2400),
      Task("Change Request".typeName, "3", "P1".projectName, "Closed".statusName, 8000, 3400),
      Task("Story".typeName, "4", "P3".projectName, "Closed".statusName, 7200, 3400))


    val map = groupingAlgorithm.groupAndCountHoursByType(tasks)
    map should have size (3)
    map should contain key "Bug".typeName
    map.get("Bug".typeName) should be(Some(3))

    map should contain key "Change Request".typeName
    map.get("Change Request".typeName) should be(Some(2.22))

    map should contain key "Story".typeName
    map.get("Story".typeName) should be(Some(2))

  }

  "it" should "not round hours " in {
    val tasks = List(Task("Bug".typeName, "1", "P1".projectName, "To Do".statusName, 1250, 2400), Task("Bug".typeName, "2", "P2".projectName, "To Do".statusName, 1250, 2400))

    val map = groupingAlgorithm.groupAndCountHoursByType(tasks)
    map should have size (1)
    map should contain key "Bug".typeName
    map.get("Bug".typeName) should be(Some(0.69))

  }


  "it" should "return empty map with values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountHoursByType(tasks)
    map should have size (0)


  }


  "An example task list " should "be grouped by project and than by task type and have valid number of hours" in {
    val tasks = List(
      Task("Bug".typeName, "1", "A".projectName, "To Do".statusName, 3600, 2400),
      Task("Bug".typeName, "1", "A".projectName, "To Do".statusName, 7200, 2400),
      Task("Bug".typeName, "2", "B".projectName, "To Do".statusName, 7200, 2400),
      Task("Change Request".typeName, "3", "A".projectName, "Closed".statusName, 8000, 3400),
      Task("Story".typeName, "4", "A".projectName, "Closed".statusName, 7200, 3400))


    val map = groupingAlgorithm.groupAndCountHoursByProjectAndType(tasks)
    map should have size (2)
    map should contain key "A".projectName
    map should contain key "B".projectName

    val a = map.get("A".projectName).get

    a should contain key "Bug".typeName
    a.get("Bug".typeName) should be(Some(3.0))

    a should contain key "Change Request".typeName
    a.get("Change Request".typeName) should be(Some(2.22))

    a should contain key "Story".typeName
    a.get("Story".typeName) should be(Some(2.0))

    val b = map.get("B".projectName).get

    b should contain key "Bug".typeName
    b.get("Bug".typeName) should be(Some(2.0))

    b should not(contain key "Change Request".typeName)
    b should not(contain key "Story".typeName)


  }

  "it" should "return empty map after grouping with values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountHoursByProjectAndType(tasks)
    map should have size (0)


  }

  "it" should "not round hours after grouping " in {
    val tasks = List(Task("Bug".typeName, "1", "P".projectName, "To Do".statusName, 1250, 2400), Task("Bug".typeName, "2", "P".projectName, "To Do".statusName, 1250, 2400))

    val map = groupingAlgorithm.groupAndCountHoursByProjectAndType(tasks)
    map should have size (1)
    map should contain key "P".projectName

    val p = map.get("P".projectName).get

    p should contain key "Bug".typeName
    p.get("Bug".typeName) should be(Some(0.69))

  }

  "Finished task " should " be filtered" in {
    val tasks = List(Task("Bug".typeName, "1", "P".projectName, "To Do".statusName, 1250, 2400), Task("Bug".typeName, "2", "P".projectName, "Business done".statusName, 1250, 2400))
    val filtered = groupingAlgorithm.filterDone(tasks)

    filtered should have size 1
    filtered(0) should be(Task("Bug".typeName, "2", "P".projectName, "Business done".statusName, 1250, 2400))

  }

  "An example task list with work log " should "be grouped task types and have valid data" in {
    val tasks = List(
      Task("Story".typeName, "1", "P1".projectName, "To Do".statusName, 1250, 2400),
      Task("Story".typeName, "2", "P1".projectName, "To Do".statusName, 2250, 3400),
      Task("Story".typeName, "Bufor", "P1".projectName, "To Do".statusName, 2250, 3400),
      Task("Bug".typeName, "2", "P2".projectName, "To Do".statusName, 1250, 2400))

    val map = groupingAlgorithm.groupAndCountWorkByType(tasks)

    map.get("Story".typeName) should be(Some(2.56))
    map.get("Change request".typeName) should be(None)
    map.get("Bug".typeName) should be(Some(0.67))

  }

  "An example task list with work log " should "be grouped by project and than by task type and have valid number of hours" in {
    val tasks = List(
      Task("Bug".typeName, "1", "A".projectName, "To Do".statusName, 3600, 2400),
      Task("Bug".typeName, "Bufor", "A".projectName, "To Do".statusName, 7200, 2400),
      Task("Bug".typeName, "2", "B".projectName, "To Do".statusName, 7200, 2400),
      Task("Change Request".typeName, "3", "A".projectName, "Closed".statusName, 8000, 3400),
      Task("Story".typeName, "4", "A".projectName, "Closed".statusName, 7200, 3400))


    val map = groupingAlgorithm.groupAndCountWorkByProjectAndType(tasks)
    map should have size (2)
    map should contain key "A".projectName
    map should contain key "B".projectName

    val a = map.get("A".projectName).get

    a should contain key "Bug".typeName
    a.get("Bug".typeName) should be(Some(1.33))

    a should contain key "Change Request".typeName
    a.get("Change Request".typeName) should be(Some(0.94))

    a should contain key "Story".typeName
    a.get("Story".typeName) should be(Some(0.94))

    val b = map.get("B".projectName).get

    b should contain key "Bug".typeName
    b.get("Bug".typeName) should be(Some(0.67))

    b should not(contain key "Change Request".typeName)
    b should not(contain key "Story".typeName)


  }
}
