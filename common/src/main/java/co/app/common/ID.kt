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

package co.app.common

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

class ID() : Parcelable {
    var id: String? = null
        set(value) {
            if (UUID.fromString(value) is UUID)
                field = value
            else throw IllegalArgumentException("id is not uuid")
        }
        get() {
            if (field == null) throw IllegalStateException("id is not generated or set")
            return field
        }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ID

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ID> {

        fun generate(): ID {
            val id = ID()
            id.id = UUID.randomUUID().toString()
            return id
        }

        fun from(value: String): ID {
            val id = ID()
            id.id = value
            return id
        }

        override fun createFromParcel(parcel: Parcel): ID = ID(parcel)

        override fun newArray(size: Int): Array<ID?> = arrayOfNulls(size)
    }

    class IDTypeAdapter : TypeAdapter<ID>() {

        @Throws(IOException::class)
        override fun read(reader: JsonReader): ID {
            val id1 = ID()
            reader.beginObject()
            var fieldname: String? = null
            while (reader.hasNext()) {
                val token: JsonToken = reader.peek()
                //get the current token
                if (token == JsonToken.NAME) fieldname = reader.nextName()
                if ("uId" == fieldname) id1.id = reader.nextString()
            }
            reader.endObject()
            return id1
        }

        @Throws(IOException::class)
        override fun write(writer: JsonWriter, id1: ID) {
            writer.beginObject()
            writer.name("uId")
            writer.value(id1.id)
            writer.endObject()
        }
    }

}