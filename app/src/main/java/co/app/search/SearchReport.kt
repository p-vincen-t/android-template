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
import android.util.ArrayMap
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import co.app.App
import co.app.AppLoaderProgress
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.dsl.prepareAdapter
import co.app.report.Report
import co.app.report.ReportHolder
import co.app.report.ReportMeta
import co.app.report.ReportView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.report_search.*
import promise.commons.AndroidPromise
import promise.ui.adapter.PromiseAdapter
import java.lang.ref.WeakReference
import promise.commons.model.List as PromiseList

@ReportMeta
class SearchReport(
    private val lifecycleOwner: LifecycleOwner,
    private val app: App,
    private val searchRepository: SearchRepository,
    private val androidPromise: AndroidPromise
) : Report, LayoutContainer {

    lateinit var view: View

    var context: Context? = null

    var search: Search? = null

    fun search(context: Context, search: Search) {
        this.context = context
        this.search = search
        if (search.query.isEmpty() && !search.recent) {
            diffAdapter.clear()
            loading_layout.showEmpty(
                R.drawable.ic_hourglass_empty_icon_24dp,
                "Type keyword to continue",
                ""
            )
            return
        }
        loading_layout.showLoading(AppLoaderProgress("Loading reports, please wait ..."))
        searchRepository.search(WeakReference(context), search).fold({

        }, {
            androidPromise.executeOnUi {
                loading_layout.showEmpty(
                    R.drawable.ic_error_icon_24dp,
                    "Problem retrieving reports ", it.message
                )
            }
        })
    }

    lateinit var diffAdapter: PromiseAdapter<ReportHolder>

    companion object {

        var searchViewMappers: PromiseList<Pair<String, ((Map<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit)>>? =
            null
    }

    override fun bind(reportView: ReportView, view: View) {
        this.view = view
        diffAdapter = search_results_recycler_view.prepareAdapter()
        searchRepository.searchResults.observe(lifecycleOwner, Observer { map ->
            if (map.isEmpty()) {
                loading_layout.showEmpty(
                    R.drawable.ic_hourglass_empty_icon_24dp,
                    "No results found",
                    "We could not find anything relating to ${this.search?.query}"
                )
                return@Observer
            }
            androidPromise.execute {
                if (searchViewMappers == null) {
                    val v =
                        PromiseList<Pair<String, ((Map<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit)>>()
                    app.modules.forEach {
                        val pair = it.value.onRegisterSearchableViews(WeakReference(context!!))
                        if (pair != null) v.add(pair)
                    }
                    searchViewMappers = v
                }
                val viewableMappersRegistered = searchViewMappers!!
                val searchResults: ArrayList<Pair<Pair<String, Int>, SearchResult>> = ArrayList()
                map.toList().map { pair ->
                    pair.second.forEach {
                        searchResults.add(Pair(Pair(pair.first.first, pair.first.second), it))
                    }
                }

                val results = PromiseList(searchResults)
                    .filter { result ->
                        viewableMappersRegistered.anyMatch {
                            it.first == result.first.first
                        }
                    }
                    .groupBy {
                        it.first

                    }.map { category ->
                        Pair(
                            category.name().first,
                            Pair(category.name().second, category.list().map { it.second })
                        )
                    }
                    .groupBy { it.first }

                androidPromise.executeOnUi {
                    loading_layout.showContent()
                }
                diffAdapter.clear()
                diffAdapter.args = search

                results.forEach { category ->
                    val viewMapper = viewableMappersRegistered.find { it.first == category.name() }
                    val map1 = ArrayMap<Int, List<SearchResult>>()
                    category.list().forEach {
                        map1[it.second.first] = it.second.second
                    }
                    viewMapper?.second?.invoke(map1, search) { report ->
                        diffAdapter.add(ReportHolder((report)))
                    }
                }
            }
        })
    }

    override fun layout(): Int = R.layout.report_search

    override val containerView: View?
        get() = view

}