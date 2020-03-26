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

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import co.app.App
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.dsl.prepareListAdapter
import co.app.report.Report
import co.app.report.ReportMeta
import co.app.report.ReportView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.report_search.*
import promise.commons.AndroidPromise
import promise.commons.model.List
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

@ReportMeta
class SearchReport(
    private val lifecycleOwner: LifecycleOwner,
    private val app: App,
    private val searchRepository: SearchRepository,
    private val androidPromise: AndroidPromise
) : Report, LayoutContainer, DiffAdapter.Listener<SearchResultViewable> {

    lateinit var view: View

    var context: Context? = null

    var search: Search? = null

    fun search(context: Context, search: Search) {
        this.context = context
        this.search = search
        if (search.query.isEmpty()) {
            diffAdapter.clear()
            loading_layout.showEmpty(R.drawable.ic_hourglass_empty_icon_24dp,
                "Type keyword to continue",
                "")
            return
        }
        searchRepository.search(WeakReference(context), search).fold({
            androidPromise.executeOnUi {
                diffAdapter.clear()
                loading_layout.showLoading(null)
            }
        }, {
            androidPromise.executeOnUi {
                loading_layout.showEmpty(R.drawable.ic_error_icon_24dp,
                "Search not raady ",it.message)
            }
        })
    }

    lateinit var diffAdapter: DiffAdapter<SearchResultViewable>
    override fun bind(reportView: ReportView, view: View) {
        this.view = view
        diffAdapter = search_results_recycler_view.prepareListAdapter(this)

        searchRepository.searchResults.observe(lifecycleOwner, Observer { map ->
            if (map.isEmpty()) {
                loading_layout.showEmpty(R.drawable.ic_hourglass_empty_icon_24dp,
                    "No results found",
                    "We could not find anything relating to ${this.search?.query}")
                return@Observer
            }
            androidPromise.execute {
                val viewableMappersRegistered =
                    List<Pair<Pair<String, Map<Class<*>, KClass<out Viewable>>>,
                            DiffAdapter.Listener<SearchResult>>>()
                app.modules.forEach {
                    val pair = it.value.onRegisterSearchableViews(WeakReference(context!!))
                    if(pair != null) viewableMappersRegistered.add(pair)
                }

                val searchResults: List<Pair<Pair<String, Int>, SearchResult>> = List()

                map.toList().map { pair ->
                    pair.second.forEach {
                        searchResults.add(Pair(Pair(pair.first.first, pair.first.second), it))
                    }
                }
                var viewHolderMappers: List<Pair<String, Pair<Map<Class<*>,
                        KClass<out Viewable>>, DiffAdapter.Listener<in SearchResult>>>> = List()
                viewableMappersRegistered.forEach {
                    viewHolderMappers.add(Pair(it.first.first, Pair(it.first.second, it.second)))
                }

                viewHolderMappers = viewHolderMappers.joinOn(searchResults) { t, u ->
                    t.second.first.containsKey(u.second.javaClass)
                }

                val reports = searchResults.groupBy {
                    it.first
                }.map { category ->
                    val v = viewableMappersRegistered
                        .first { it.first.first == category.name().first }

                    SearchResultViewable(Pair(category.name().second,
                        category.list().map { it.second }), v.first.second, v.second
                    )
                }

                androidPromise.executeOnUi {
                    loading_layout.showContent()
                }
                diffAdapter.clear()
                diffAdapter.args = search
                diffAdapter.add(reports)
            }
        })
    }

    override fun layout(): Int = R.layout.report_search
    override val containerView: View?
        get() = view

    override fun onClick(t: SearchResultViewable, id: Int) {

    }
}