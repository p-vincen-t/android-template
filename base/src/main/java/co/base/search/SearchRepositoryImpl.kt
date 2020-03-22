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

import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.domain.message.ChatMessage
import co.app.domain.search.Search
import co.app.domain.search.SearchRepository
import co.app.domain.search.SearchResult
import co.base.message.SEARCH_ARG
import co.base.repos.RepoScope
import promise.commons.tx.PromiseCallback
import promise.model.StoreRepository
import javax.inject.Inject

@RepoScope
class SearchRepositoryImpl
@Inject constructor(private val searchRepo: StoreRepository<Search>,
                    private val messageRepo: StoreRepository<ChatMessage>) : SearchRepository {

    private val recentSearches: MutableLiveData<List<SearchResult>> by lazy { MutableLiveData<List<SearchResult>>() }

    override fun search(search: Search): PromiseCallback<LiveData<List<SearchResult>>> =
        PromiseCallback {resolve, _ ->
            recentSearches.postValue(promise.commons.model.List())
            resolve(recentSearches)
            messageRepo.all(ArrayMap<String, Any>().apply {
                put(SEARCH_ARG, search.query)
            }, { messages, _ ->
                val list: promise.commons.model.List<SearchResult> = promise.commons.model.List(recentSearches.value ?: promise.commons.model.List())
                list.addAll(messages)
                recentSearches.postValue(list)
                searchRepo.save(search, null)
            })
        }

    override fun getRecentSearches(): PromiseCallback<LiveData<List<SearchResult>>> =
        PromiseCallback { resolve, reject ->
            resolve(recentSearches)

        }

    override fun clearHistory(): PromiseCallback<Boolean> = PromiseCallback { resolve, reject ->
        searchRepo.clear(null, {
            resolve(true)
        }, {
            reject(it)
        })
    }
}