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

  var exampleFis: BufferedInputStream = null


  override def beforeEach() {
    exampleFis = new BufferedInputStream(new FileInputStream(new File("./src/test/resources/report.xls")))
  }

  override def afterEach() {
    exampleFis.close()
  }
}
