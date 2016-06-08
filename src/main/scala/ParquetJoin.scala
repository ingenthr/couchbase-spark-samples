/*
 * Copyright (c) 2015 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.couchbase.spark.sql._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.sources.EqualTo
import org.apache.spark.{SparkConf, SparkContext}

object ParquetJoin {

  def main(args: Array[String]): Unit = {

    // Configure Spark
    val cfg = new SparkConf()
      .setAppName("n1qlQueryExample")
      .setMaster("local[*]")
      .set("com.couchbase.bucket.travel-sample", "")

    // Generate The Context
    val sc = new SparkContext(cfg)

    // Spark SQL Setup
    val sql = new SQLContext(sc)



    // Step 1, (comment me out) -> store landmarks in parquet
//    val landmarks = sql.read.couchbase(schemaFilter = EqualTo("type", "landmark"))
//    landmarks.write.parquet("landmarks")

    val landmarks = sql.read.parquet("landmarks")

    // Load Airports from Couchbase
    val airports = sql.read.couchbase(schemaFilter = EqualTo("type", "airport"))

    // find all landmarks in the same city as the given FAA code
    val toFind = "SFO" // try SFO or LAX

    airports
      .join(landmarks, airports("city") === landmarks("city"))
      .where(airports("faa") === toFind and landmarks("url").isNotNull)
      .select(landmarks("name"), landmarks("address"), airports("faa"))
      .orderBy(landmarks("name").asc)
      .show()

  }



}