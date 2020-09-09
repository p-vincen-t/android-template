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
import promise.commons.data.log.LogUtil
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
            return
        }
//        searchRepository.search(WeakReference(context), search).fold({
//
//        }, {
//            androidPromise.executeOnUi {
//                loading_layout.showEmpty(
//                    R.drawable.ic_error_icon_24dp,
//                    "Problem retrieving reports ", it.message
//                )
//            }
//        })

        diffAdapter.clear()
       app.modules.forEach {
           val views = it.value.onSearch(WeakReference(context), search)
           diffAdapter.add(promise.commons.model.List(views))
       }
    }

    lateinit var diffAdapter: PromiseAdapter<SearchResultsViewHolder>

    companion object {
        val TAG: String = LogUtil.makeTag(SearchReport::class.java)

    }

    override fun bind(reportView: ReportView, view: View) {
        this.view = view
        diffAdapter = search_results_recycler_view.prepareAdapter {
            args = true
        }

//        val lock = Any()
//
//        searchRepository.searchResults.observe(lifecycleOwner, Observer { map ->
//            androidPromise.execute {
//                if (searchViewMappers == null) {
//                    val v =
//                        PromiseList<Pair<String, ((Pair<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit)>>()
//                    app.modules.forEach {
//                        val pair = it.value.onRegisterSearchableViews(WeakReference(context!!))
//                        if (pair != null) v.add(pair)
//                    }
//                    diffAdapter.clear()
//                    searchViewMappers = v
//                }
//
//                val viewMapper = searchViewMappers!!.find { it.first == map.first.first }
//                val pair: Pair<Int, List<SearchResult>> = Pair(map.first.second, map.second)
//                androidPromise.executeOnUi {
//                    loading_layout.showContent()
//                }
//
//                viewMapper?.second?.invoke(pair, search) { report ->
//                    synchronized(lock) {
//                        //LogUtil.e(TAG, "adding report ", report)
//                        diffAdapter.add(ReportHolder((report)))
//                    }
//                }
//            }
//        })
    }

    override fun layout(): Int = R.layout.report_search

    override val containerView: View?
        get() = view

}