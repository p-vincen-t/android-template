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

package co.app

import android.content.Context
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.common.search.SearchableRepository
import co.app.report.Report
import java.lang.ref.WeakReference

interface ModuleRegister {

    fun onRegister(app: App)

    fun onRegisterSearchableViews(context: WeakReference<Context>):
            Pair<String, ((Map<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit)>?

    fun registerSearchableRepository(searchableRepository: SearchableRepository) {
        SearchRepository.registerSearchableRepository(searchableRepository)
    }
}