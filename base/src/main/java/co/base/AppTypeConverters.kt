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

import androidx.room.TypeConverter
import co.app.common.ID
import java.util.*

class AppTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun stringFromID(value: ID?): String? {
        if (value == null) return ID.generate().id
        return value.id
    }

    @TypeConverter
    fun stringToArray(date: String): Array<ID>? = try {
        val parts = date.split(",")
        parts.map {
            stringToID(it)
        }.toTypedArray()
    } catch (e: Exception) {
        null
    }


    @TypeConverter
    fun stringFromArray(value: Array<ID>?): String? = when (value) {
        null -> null
        else -> value.map { it.id }.joinToString()
    }

    @TypeConverter
    fun stringArrayFromIDArray(value: Array<ID>?): Array<String>? = when (value) {
        null -> null
        else -> value.map { it.id!! }.toTypedArray()
    }

    @TypeConverter
    fun stringToID(date: String): ID = ID.from(date)
}