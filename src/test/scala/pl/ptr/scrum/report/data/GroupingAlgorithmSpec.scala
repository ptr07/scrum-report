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

class GroupingAlgorithmSpec extends FlatSpec with Matchers {

  behavior of "An example GroupingAlgorithm"
  val groupingAlgorithm = new GroupingAlgorithm

  "An example task list " should "be grouped by status and have valid number of hours" in {
    val tasks = List(Task("Bug", "1", "P1", "To Do", 3600, 2400), Task("Bug", "2", "P2", "To Do", 7200, 2400), Task("Bug", "3", "P1", "Closed", 8000, 3400))


    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do"
    map.get("To Do") should be(Some(3))

  }

  "it" should "not round incomplete hours " in {
    val tasks = List(Task("Bug", "1", "P1", "To Do", 1700, 2400), Task("Bug", "1", "P1", "Developed", 1900, 2400))
    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do"
    map.get("To Do") should be(Some(0.47))

    map should contain key "Developed"
    map.get("Developed") should be(Some(0.53))

  }

  "it" should "subtract not finished buffer tasks from 'to do' and add it to 'done'" in {
    val tasks = List(Task("Bug", "bufor", "P1", "To Do", 3600, 3600), Task("Bug", "bufor", "P1", "Business done", 7200, 3600))

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do"
    map.get("To Do") should be(Some(0))

    map should contain key "Business done"
    map.get("Business done") should be(Some(3))

  }


  "it" should "not accepted exceeded buffers" in {
    val tasks = List(Task("Bug", "bufor", "P1", "To Do", 3600, 7200))

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (2)
    map should contain key "To Do"
    map.get("To Do") should be(Some(0))

    map should contain key "Business done"
    map.get("Business done") should be(Some(1))

  }


  "it" should "return map with two empty values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountByStatus(tasks)
    map should have size (0)

  }

  "An example task list " should "be grouped by task type and have valid number of hours" in {
    val tasks = List(
      Task("Bug", "1", "P1", "To Do", 3600, 2400),
      Task("Bug", "2", "P2", "To Do", 7200, 2400),
      Task("Change Request", "3", "P1", "Closed", 8000, 3400),
      Task("Story", "4", "P3", "Closed", 7200, 3400))


    val map = groupingAlgorithm.groupAndCountByType(tasks)
    map should have size (3)
    map should contain key "Bug"
    map.get("Bug") should be(Some(3))

    map should contain key "Change Request"
    map.get("Change Request") should be(Some(2.22))

    map should contain key "Story"
    map.get("Story") should be(Some(2))

  }

  "it" should "not round hours " in {
    val tasks = List(Task("Bug", "1", "P1", "To Do", 1250, 2400), Task("Bug", "2", "P2", "To Do", 1250, 2400))

    val map = groupingAlgorithm.groupAndCountByType(tasks)
    map should have size (1)
    map should contain key "Bug"
    map.get("Bug") should be(Some(0.69))

  }


  "it" should "return empty map with values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountByType(tasks)
    map should have size (0)


  }


  "An example task list " should "be grouped by project and than by task type and have valid number of hours" in {
    val tasks = List(
      Task("Bug", "1", "A", "To Do", 3600, 2400),
      Task("Bug", "1", "A", "To Do", 7200, 2400),
      Task("Bug", "2", "B", "To Do", 7200, 2400),
      Task("Change Request", "3", "A", "Closed", 8000, 3400),
      Task("Story", "4", "A", "Closed", 7200, 3400))


    val map = groupingAlgorithm.groupAndCountByProjectAndType(tasks)
    map should have size (2)
    map should contain key "A"
    map should contain key "B"

    val a = map.get("A").get

    a should contain key "Bug"
    a.get("Bug") should be(Some(3.0))

    a should contain key "Change Request"
    a.get("Change Request") should be(Some(2.22))

    a should contain key "Story"
    a.get("Story") should be(Some(2.0))

    val b = map.get("B").get

    b should contain key "Bug"
    b.get("Bug") should be(Some(2.0))

    b should not(contain key "Change Request")
    b should not(contain key "Story")


  }

  "it" should "return empty map after grouping with values for empty list" in {
    val tasks = List[Task]()

    val map = groupingAlgorithm.groupAndCountByProjectAndType(tasks)
    map should have size (0)


  }

  "it" should "not round hours after grouping " in {
    val tasks = List(Task("Bug", "1", "P", "To Do", 1250, 2400), Task("Bug", "2", "P", "To Do", 1250, 2400))

    val map = groupingAlgorithm.groupAndCountByProjectAndType(tasks)
    map should have size (1)
    map should contain key "P"

    val p = map.get("P").get

    p should contain key "Bug"
    p.get("Bug") should be(Some(0.69))

  }

}
