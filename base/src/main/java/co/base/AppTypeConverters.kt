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

package co.base

import co.app.common.ID
import promise.database.TypeConverter
import java.util.*

@TypeConverter
class AppTypeConverters {

    fun fromTimestamp(value: String?): Date? = value?.let { Date(it.toLong()) }

    fun dateToTimestamp(date: Date?): String? = date?.time.toString()

    fun stringFromID(value: ID?): String? {
        if (value == null) return ID.generate().id
        return value.id
    }

    fun stringToArray(date: String): Array<ID>? = try {
        val parts = date.split(",")
        parts.map {
            stringToID(it)
        }.toTypedArray()
    } catch (e: Exception) {
        null
    }

    fun stringArrayFromIDArray(value: Array<ID>?): String? = when (value) {
        null -> null
        else -> {
            val builder = StringBuilder()
            value.mapIndexed { index, id ->
                builder.append(id.id)
                if (index != value.size - 1) {
                    builder.append(",")
                }
            }
            builder.toString()
        }
    }

    fun stringToID(date: String): ID = ID.from(date)
}