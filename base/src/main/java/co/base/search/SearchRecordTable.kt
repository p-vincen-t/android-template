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

package co.base.search

import android.content.ContentValues
import android.database.Cursor
import promise.commons.model.List
import promise.db.*

@Table(
    tableName = "search"
)
class SearchRecordTable(database: FastDatabase): FastTable<SearchRecord>(database) {
    override val columns: List<out Column<*>>
        get() = List.fromArray(queryColumn)

    override fun deserialize(e: Cursor): SearchRecord = SearchRecord().apply {
        query = e.getString(queryColumn.index)
    }

    override fun serialize(t: SearchRecord): ContentValues = ContentValues().apply {
        put(queryColumn.name, t.query)
    }

    companion object {
        val queryColumn: Column<String> = Column("query", Column.Type.TEXT.NOT_NULL(), 1)
    }
}