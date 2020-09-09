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

package co.app.app

import co.app.SplashActivity
import co.app.dashboard.DashboardActivity
import co.app.dashboard.main.MainFragment
import co.app.messaging.chat.ChatActivity
import co.app.messaging.chat.ChatService
import co.app.messaging.chat.ChatThreadFragment
import co.app.search.SearchActivity
import co.base.DataScope
import co.base.RepoBinders
import co.base.RepoScope
import co.base.ReposModule
import co.base.message.MessagesModule
import co.base.search.SearchModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UIBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeDashboardActivity(): DashboardActivity

    @ContributesAndroidInjector(
        modules = [
            RepoBinders::class
        ]
    )
    @DataScope
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector(
        modules = [
            RepoBinders::class
        ]
    )
    @DataScope
    abstract fun contributeSearchActivity(): SearchActivity

    @ContributesAndroidInjector(
        modules = [
            RepoBinders::class
        ]
    )
    @DataScope
    abstract fun contributeChatService(): ChatService

    @ContributesAndroidInjector(
        modules = [
            RepoBinders::class
        ]
    )
    @DataScope
    abstract fun contributeChatThreadFragment(): ChatThreadFragment

    @ContributesAndroidInjector
    abstract fun contributeSplashActivity(): SplashActivity

}