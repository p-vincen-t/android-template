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

package co.base.common

import android.content.ContentValues
import android.database.Cursor
import co.app.common.ID
import promise.commons.model.List
import promise.db.Column
import promise.db.FastDatabase
import promise.db.FastTable
import promise.db.Table
import promise.db.query.criteria.Criteria

@Table(
    tableName = "app_photos",
    indexes = [
        Table.Index(columnName = "refId"),
        Table.Index(columnName = "refName")
    ]
)
class PhotoRecordTable(database: FastDatabase) : FastTable<PhotoRecord>(database) {

    fun getPhotoRecordByRef(refName: String, id: ID): PhotoRecord? =
        findOne(refNameColumn.with(refName), refIdColumn.with(id.id))

    fun getPhotoRecordsByRef(refName: String, ids: Array<ID>): List<PhotoRecord> {
        val cursor = query(queryBuilder()
            .whereAnd(Criteria.equals(refNameColumn, refName))
            .whereAnd(Criteria.`in`(refIdColumn, List(ids.map { it.id }).toArray()))
            .distinct())
        cursor.moveToFirst()
        val records = List<PhotoRecord>()
        while (cursor.moveToNext()) {
            records.add(deserialize(cursor).apply {
                uid = cursor.getInt(id.index)
            })
        }
        cursor.close()
        return records
    }

    override val columns: List<out Column<*>>
        get() = List.fromArray(refIdColumn, refNameColumn, urlColumn, typeColumn)

    override fun deserialize(e: Cursor): PhotoRecord = PhotoRecord().apply {
        refId = ID.from(e.getString(refIdColumn.index))
        refName = e.getString(refNameColumn.index)
        url = e.getString(urlColumn.index)
        type = e.getString(typeColumn.index)
    }

    override fun serialize(t: PhotoRecord): ContentValues = ContentValues().apply {
        put(refIdColumn.name, t.refId!!.id)
        put(refNameColumn.name, t.refName)
        put(urlColumn.name, t.url)
        put(typeColumn.name, t.type)
    }

    companion object {
        val refIdColumn = Column<String>("refId", Column.Type.TEXT.NOT_NULL(), 1)
        val refNameColumn = Column<String>("refName", Column.Type.TEXT.NOT_NULL(), 2)
        val urlColumn = Column<String>("url", Column.Type.TEXT.NOT_NULL(), 3)
        val typeColumn = Column<String>("type", Column.Type.TEXT.NOT_NULL(), 4)
    }

}