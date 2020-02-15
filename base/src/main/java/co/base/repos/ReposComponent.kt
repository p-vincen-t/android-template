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

package co.base.repos

import co.app.common.account.UserAccount
import co.app.domain.message.MessageRepository
import co.base.data.DataComponent
import dagger.BindsInstance
import dagger.Component

@RepoScope
@Component(
    dependencies = [DataComponent::class],
    modules = [RepoBinders::class, ReposModule::class]
)
interface ReposComponent {

    fun messageRepository(): MessageRepository

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance userAccount: UserAccount, dataComponent: DataComponent): ReposComponent
    }
}