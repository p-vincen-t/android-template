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

package com.app.wallet.base.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.wallet.base.record.RecordData
import com.app.wallet.base.record.RecordsDao

@Database(entities = [RecordData::class], version = 1)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun recordsDao(): RecordsDao

    companion object {
        @Volatile
        var instance: WalletDatabase? = null

        private var LOCK = Any()

        operator fun invoke(application: Application, name: String): WalletDatabase = instance
            ?: synchronized(LOCK) {
               instance?: Room.databaseBuilder(
                    application.applicationContext,
                    WalletDatabase::class.java,
                    name
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        instance = it
                    }
            }
    }
}