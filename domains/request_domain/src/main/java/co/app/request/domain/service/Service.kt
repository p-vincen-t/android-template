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

package co.app.request.domain.service

import co.app.common.account.AppUser
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.request.domain.Pricing
import promise.commons.model.Identifiable

class Service constructor(
    val registrar: AppUser,
    val category: CharSequence,
    val name: CharSequence,
    val description: CharSequence,
    val active: Boolean
) : Identifiable<String>, SearchResult {
    val price: Pricing? = null
    var uId: String? = null
    override fun setId(t: String) {
        this.uId = t
    }

    override fun getId(): String = uId!!
    override fun onSearch(search: Search): Boolean {
        TODO("Not yet implemented")
    }
}