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

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.common.addValue
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.base.repos.RepoScope
import promise.commons.tx.AsyncEither
import promise.commons.tx.Either
import promise.model.Repository
import javax.inject.Inject

@RepoScope
class SearchRepositoryImpl
@Inject constructor(
    private val searchRepo: Repository<Search>
) : SearchRepository() {


    private val recentSearchResultsMutable: MutableLiveData<Map<String, List<SearchResult>>> =
        MutableLiveData()

    private val recentSearchMutable: MutableLiveData<List<Search>> = MutableLiveData()

    override val searchResults: LiveData<Map<String, List<SearchResult>>>
        get() = recentSearchResultsMutable

    override fun recentSearchQueries(): LiveData<List<Search>> = recentSearchMutable

    override fun search(search: Search): Either<Any, Throwable> = AsyncEither { resolve, reject ->
        resolve("searching ${searchRepositories.size} repositories")
        val lock = Any()
        val results: promise.commons.model.List<Map<String, List<SearchResult>>> =
            promise.commons.model.List()
        searchRepositories.forEach { repository ->
            repository.onSearch(search).fold({
                synchronized(lock) {
                    results.add(it)
                    val mapResults = ArrayMap<String, List<SearchResult>>()
                    results.forEach {
                        mapResults.putAll(it)
                    }
                    recentSearchResultsMutable.postValue(mapResults)
                }
            }, {
                reject(it)
            })
        }
        recentSearchMutable.addValue(search)
        searchRepo.save(search, null)
    }

    override fun clearHistory(): Either<Boolean, Throwable> = AsyncEither { resolve, reject ->
        searchRepo.clear(null, {
            recentSearchMutable.postValue(listOf())
            resolve(true)
        }, {
            reject(it)
        })
    }
}