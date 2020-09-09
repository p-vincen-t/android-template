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

import android.content.Context
import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.common.addValue
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.base.DataScope
import co.base.RepoScope
import promise.commons.data.log.LogUtil
import promise.commons.tx.AsyncEither
import promise.commons.tx.Either
import promise.model.Repository
import java.lang.ref.WeakReference
import javax.inject.Inject

@DataScope
class SearchRepositoryImpl
@Inject constructor(
    private val searchRepo: Repository<Search>
) : SearchRepository() {

    private val recentSearchResultsMutable: MutableLiveData<Pair<Pair<String, Int>, List<SearchResult>>> =
        MutableLiveData()

    private val recentSearchMutable: MutableLiveData<List<Search>> = MutableLiveData()

    override val searchResults: LiveData<Pair<Pair<String, Int>, List<SearchResult>>>
        get() = recentSearchResultsMutable

    override fun recentSearchQueries(): LiveData<List<Search>> = recentSearchMutable


    override fun searchSuggestions(search: Search): LiveData<List<Search>> {
        TODO("Not yet implemented")
    }

    override fun search(context: WeakReference<Context>, search: Search): Either<Any> =
        AsyncEither { resolve, reject ->
            if (searchRepositories.isEmpty()) {
                reject(Exception("Search not ready, data sources not registered"))
                return@AsyncEither
            }
            resolve(Any())
            LogUtil.e(TAG, "searching ${searchRepositories.size} repositories")
            val lock = Any()
            searchRepositories.forEach { repository ->
                repository.onSearch(context, search).yield({ result ->
                    synchronized(lock) {
                        if (result == null) return@synchronized
                        recentSearchResultsMutable.postValue(result)
                    }
                }, {
                    reject(it)
                })
            }
            recentSearchMutable.addValue(search)
            searchRepo.save(search, null)
        }

    override fun clearHistory(): Either<Boolean> = AsyncEither { resolve, reject ->
        searchRepo.clear(null, {
            recentSearchMutable.postValue(listOf())
            resolve(true)
        }, {
            reject(it)
        })
    }

    companion object {
        val TAG: String = LogUtil.makeTag(SearchRepositoryImpl::class.java)
    }
}