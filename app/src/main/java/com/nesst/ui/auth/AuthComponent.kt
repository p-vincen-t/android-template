/*
 * Copyright 2017, Nesst
 * Licensed under the Apache License, Version 2.0, "Nesst Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesst.ui.auth

import com.nesstbase.session.SessionComponent
import dagger.Component

@Component(dependencies = [SessionComponent::class])
@AuthScope
interface AuthComponent {
    fun inject(authActivity: AuthActivity)
}