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

package com.base.repos

import com.app.domain.session.Session
import com.base.data.DataComponent
import dagger.BindsInstance
import dagger.Component

@RepoScope
@Component(dependencies = [DataComponent::class], modules = [ReposModule::class])
interface ReposComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance session: Session, dataComponent: DataComponent): ReposComponent
    }
}