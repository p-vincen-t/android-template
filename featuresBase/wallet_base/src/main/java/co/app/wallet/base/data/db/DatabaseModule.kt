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

package co.app.wallet.base.data.db

import co.app.wallet.base.record.RecordsDao
import dagger.Module
import dagger.Provides
import promise.commons.AndroidPromise

private const val DB_NAME = "wallet_db"

@Module
object DatabaseModule {

    @Provides
    @JvmStatic
    fun provideDatabase(promise: AndroidPromise): WalletDatabase =
        WalletDatabase(
            promise.context(),
            DB_NAME
        )

    @Provides
    @JvmStatic
    fun provideRecordsDao(database: WalletDatabase): RecordsDao = database.recordsDao()
}