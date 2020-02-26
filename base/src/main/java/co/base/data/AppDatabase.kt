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

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.base.message.*

@Database(
    entities = [
        ChatMessageRecord::class,
        ChatUserRecord::class
    ],
    version = 1
)
@TypeConverters(co.base.data.TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageRecordDao

    abstract fun messageThreadDao(): MessageThreadRecordDao

    abstract fun chatUserDao(): ChatUserDao

    companion object {
        @Volatile
        var instance: AppDatabase? = null

        private var LOCK = Any()

        operator fun invoke(application: Application, name: String): AppDatabase = instance
            ?: synchronized(LOCK) {
                instance ?: Room.databaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java,
                    name
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        instance = it
                    }
            }

        operator fun invoke(application: Application): AppDatabase = instance
            ?: synchronized(LOCK) {
                instance ?: Room.inMemoryDatabaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        instance = it
                    }
            }
    }
}