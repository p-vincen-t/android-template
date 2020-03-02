/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.base.data

import android.database.sqlite.SQLiteDatabase
import co.base.search.SearchRecordTable
import promise.commons.model.List
import promise.db.FastDatabase
import promise.db.Table

class BehaviourDatabase private constructor(val name: String) : FastDatabase(
    name,
    version, null, null
) {

    companion object {

        const val version = 1

        @Volatile
        var instance: BehaviourDatabase? = null

        private var LOCK = Any()

        operator fun invoke(name: String): BehaviourDatabase = instance
            ?: synchronized(LOCK) {
                instance ?: BehaviourDatabase(
                    name
                )
                    .also {
                        instance = it
                    }
            }

        val searchRecordTable: SearchRecordTable by lazy {
            SearchRecordTable(instance!!)
        }
    }

    override fun tables(): List<Table<*, in SQLiteDatabase>> = List.fromArray(searchRecordTable)
}