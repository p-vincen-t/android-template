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

package co.app.wallet

import android.content.Context
import co.app.App
import co.app.ModuleRegister
import co.app.common.search.SearchResult
import co.app.report.Report
import co.app.wallet.base.data.DaggerDataComponent
import co.app.wallet.base.data.DataComponent
import java.lang.ref.WeakReference

class ModuleRegistrar : ModuleRegister() {

    lateinit var dataComponent: DataComponent

    override fun onRegister(app: App) {
        Companion.app = app
        dataComponent = DaggerDataComponent.factory()
            .create(app.promise, app.gson(), app.okHttpClient())
        //registerSearchableRepository(dataComponent.accountsRepository())
    }

    override fun onRegisterSearchableViews(context: WeakReference<Context>): Pair<String, (Pair<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit>? {
        return super.onRegisterSearchableViews(context)
    }

    companion object {
        lateinit var app: App
        fun instance(): ModuleRegistrar = app.modules[App.WALLET_FEATURE_NAME] as ModuleRegistrar
    }
}

