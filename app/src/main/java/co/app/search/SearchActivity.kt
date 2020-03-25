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

package co.app.search

import android.os.Bundle
import androidx.lifecycle.Observer
import co.app.BaseActivity
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.dsl.prepareListAdapter
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import promise.commons.model.List
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter
import javax.inject.Inject
import kotlin.reflect.KClass

class SearchActivity : BaseActivity(), SearchForm.Listener, DiffAdapter.Listener<SearchResultViewable> {

    lateinit var searchForm: SearchForm

    @Inject
    lateinit var searchRepository: SearchRepository

    @Inject
    lateinit var androidPromise: AndroidPromise

    lateinit var diffAdapter: DiffAdapter<SearchResultViewable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        addBackButton()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        LogUtil.e(TAG, "search form init")
        searchForm = SearchForm(this, search_fab, this)
        search_report.report = searchForm
        diffAdapter = search_results_recycler_view.prepareListAdapter(this)

        searchRepository.searchResults.observe(this, Observer { map ->
            androidPromise.execute {
                val viewables = List<Pair<Map<Class<out SearchResult>,
                        KClass<out Viewable>>,
                        DiffAdapter.Listener<in SearchResult>>>()
                app.modules.forEach {
                    val pair =  it.value.registerSearchViewables(this)
                    viewables.add(pair)
                }
                val searchResults: List<SearchResult> = List()
                map.toList().map {
                    searchResults.addAll(it.second)
                }
                var viewHolderMappers: List<Pair<Class<out SearchResult>, KClass<out Viewable>>> = List()
                viewables.forEach {
                    viewHolderMappers.addAll(it.first.toList())
                }

                viewHolderMappers = viewHolderMappers.joinOn(searchResults) { t, u ->
                    t.first == u.javaClass
                }


                androidPromise.executeOnUi {
                    diffAdapter.clear()
                    diffAdapter.add()
                }
            }
        })
    }

    override fun onSearch(search: Search) {
        toolbar_layout.title = search.query
        searchRepository.search(search).fold({}, {})
    }

    companion object {
        val TAG: String = LogUtil.makeTag(SearchActivity::class.java)
    }

    override fun onClick(t: SearchResultViewable, id: Int) {

    }

}
