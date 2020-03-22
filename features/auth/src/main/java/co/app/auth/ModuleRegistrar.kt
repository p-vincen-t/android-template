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

package co.app.auth

import co.app.App
import co.app.ModuleRegister
import co.app.auth.base.DaggerSessionComponent
import co.app.auth.base.SessionComponent
import promise.commons.data.log.LogUtil

class ModuleRegistrar : ModuleRegister {

    lateinit var sessionComponent: SessionComponent

    override fun register(app: App) {
        LogUtil.e("Auth", "registering auth module : ")
        ModuleRegistrar.app = app
        sessionComponent = DaggerSessionComponent.factory()
            .create(
                app.okHttpClient(),
                app.apiUrl(),
                app.appComponent
            )

    }

    companion object {
        lateinit var app: App
        fun instance(): ModuleRegistrar = app.modules[App.AUTH_FEATURE_NAME] as ModuleRegistrar
    }
}