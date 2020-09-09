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

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.app.R
import co.app.common.search.SearchResultViewable
import co.app.dsl.Grid
import co.app.dsl.LayoutType
import co.app.dsl.Linear
import co.app.dsl.prepareListAdapter
import com.facebook.shimmer.ShimmerFrameLayout
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter

class SearchResultsViewHolder(
    var views: List<SearchResultViewable>,
    private val layoutType: LayoutType = Linear()
) : Viewable {

    lateinit var recyclerView: RecyclerView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var adapter: DiffAdapter<SearchResultViewable>

    fun setSearchedItems(views: List<SearchResultViewable>) {
        shimmerFrameLayout.stopShimmer()
        adapter.setList(promise.commons.model.List(views))
    }

    fun setListener(listener1: DiffAdapter.Listener<SearchResultViewable>) {
        adapter.listener = listener1
    }

    fun reInitSearch() {
        shimmerFrameLayout.startShimmer()
    }

    override fun init(view: View) {
        recyclerView = view.findViewById(R.id.search_results_recycler_view)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_frame_layout)
        adapter = recyclerView.prepareListAdapter(
            layoutManager = when (layoutType) {
                is Linear -> LinearLayoutManager(
                    view.context,
                    layoutType.orientation,
                    false
                )
                is Grid -> GridLayoutManager(
                    view.context,
                    layoutType.spanCount
                )
            }
        )/**/
    }

    override fun bind(view: View, args: Any?) {
        if (args is Boolean && args) reInitSearch()
        else setSearchedItems(views)
    }

    override fun layout(): Int = R.layout.search_results_layout

}

