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

package com.app.wallet

import com.app.App
import com.app.ModuleRegister
import com.app.wallet.base.WalletBase

class ModuleRegistrar : WalletBase(), ModuleRegister {

    override fun register(app: App) {
        ModuleRegistrar.app = app
        init(app.promise,
            app.gson(),
            app.okHttpClient())
    }

    companion object {
        lateinit var app: App
        fun instance(): ModuleRegistrar = app.modules[App.WALLET_FEATURE_NAME] as ModuleRegistrar
    }
}

