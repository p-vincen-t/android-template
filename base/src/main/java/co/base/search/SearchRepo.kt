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

package co.base.search

import co.app.domain.search.Search
import promise.commons.model.List
import promise.model.IdentifiableList
import promise.model.AbstractAsyncIDataStore
import promise.model.AbstractSyncIDataStore

class SyncSearchRepo(private val searchRecordTable: SearchRecordTable) : AbstractSyncIDataStore<Search>() {
    override fun save(t: List<out Search>, args: Map<String, Any?>?): Any? {
        return searchRecordTable.save(IdentifiableList<SearchRecord>(t.map {
            SearchRecord.from(it)
        }))
    }

    override fun save(t: Search, args: Map<String, Any?>?): Pair<Search, Any?> {
        return Pair(t, searchRecordTable.save(SearchRecord.from(t)))
    }
}

class AsyncSearchRepo : AbstractAsyncIDataStore<Search>()
