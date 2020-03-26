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
import co.app.R
import co.app.common.search.SearchResult
import co.app.dsl.prepareListAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_result_item.*
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter
import kotlin.reflect.KClass

class SearchResultViewable(
    private val pair: Pair<Int, List<SearchResult>>,
    private val viewMapper: Map<Class<*>, KClass<out Viewable>>,
    private val adapterListener: DiffAdapter.Listener<SearchResult>
) : Viewable, LayoutContainer {

    private lateinit var view: View

    override fun layout(): Int = R.layout.search_result_item

    override fun bind(view: View?, args1: Any) {
        search_title.setText(pair.first)
        val adapter =
            search_results_list.prepareListAdapter(adapterListener, viewableClasses = viewMapper) {
                args = args1
            }
        adapter.add(promise.commons.model.List(pair.second))
    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}