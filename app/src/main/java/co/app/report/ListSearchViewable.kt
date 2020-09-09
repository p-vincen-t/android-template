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

package co.app.report

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.app.R
import co.app.common.search.SearchResultViewable
import co.app.dsl.Grid
import co.app.dsl.LayoutType
import co.app.dsl.Linear
import co.app.dsl.prepareListAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_search_results_view.*
import kotlinx.android.synthetic.main.list_view.footer_view
import kotlinx.android.synthetic.main.list_view.list_recycler_view
import promise.commons.data.log.LogUtil
import promise.commons.model.List
import promise.commons.tx.PromiseResult
import promise.ui.Viewable
import promise.ui.adapter.DataSource
import promise.ui.adapter.DiffAdapter
import kotlin.reflect.KClass

class ListSearchViewable<T : Any>(
    private val title: String? = null,
    @StringRes private val titleRs: Int = 0,
    private val map: Map<Class<*>, KClass<out Viewable>>? = null,
    private val listData: List<T>? = null,
    private val dataSource: DataSource<T>? = null,
    private val listener: DiffAdapter.Listener<T>? = null,
    @ListReport.Companion.OrientationType
    private val listOrientation: Int = RecyclerView.VERTICAL,
    private val layoutType: LayoutType? = null,
    private val dataArgs: Any? = null,
    private val visibleThreshold: Int = 10,
    private val menuRs: Int = 0,
    private val menuClickListener: Toolbar.OnMenuItemClickListener? = null,
    private var footerParams: FooterParams? = null
) : SearchResultViewable(), LayoutContainer {

    var view: View? = null

    lateinit var adapter: DiffAdapter<T>

    fun updateList(listData: List<T>) {

        adapter.setList(listData)
    }

    override fun layout(): Int = R.layout.list_search_results_view

    override fun hashCode(): Int = listData?.hashCode() ?: 0

    override fun toString(): String = "ListReport(title=$title," +
            "listData=$listData," +
            " dataArgs=$dataArgs)"

    override fun init(view: View) {
        this.view = view
    }

    override fun bind(view: View, args: Any?) {

        if (title != null) {
            LogUtil.e(TAG, "title ", title)
            toolbar.title = title
        }
        if (menuRs != 0 && menuClickListener != null) {
            toolbar.inflateMenu(menuRs)
            toolbar.setOnMenuItemClickListener(menuClickListener)
        }
        adapter = list_recycler_view.prepareListAdapter(
            viewableClasses = map,
            listener = listener,
            orientation = listOrientation,
            layoutManager = when (layoutType) {
                null -> LinearLayoutManager(view.context)
                is Linear -> LinearLayoutManager(
                    view.context,
                    layoutType.orientation,
                    false
                )
                else -> GridLayoutManager(
                    view.context,
                    (layoutType as Grid).spanCount
                )
            }
        ) {
            if (dataSource != null) withPagination(
                dataSource,
                ListReportLoader(),
                visibleThreshold = visibleThreshold
            )
        }
        if (listData != null) {
            //LogUtil.e(TAG, "data ", listData)
            adapter.clear()
            adapter.add(listData)
        } else if (dataSource != null) {
            adapter.addLoadingView()
            dataSource.load(PromiseResult<List<T>, Throwable>()
                .withCallback {
                    updateList(it)
                    if (adapter.getList().isNotEmpty())
                        adapter.removeLoader()
                    LogUtil.e(TAG, "data from loader ", it)
                }
                .withErrorCallback {
                    LogUtil.e(TAG, it)
                }, 0,
                visibleThreshold
            )
        }

        if (footerParams != null) {
            LayoutInflater.from(view.context)
                .inflate(footerParams!!.footerLayout, footer_view, true)
            if (footerParams!!.clickListener != null && footerParams!!.viewIds != null) footerParams!!.viewIds!!.forEach {
                footer_view.findViewById<View>(it).setOnClickListener(footerParams!!.clickListener)
            }
        }
    }

    override val containerView: View?
        get() = view

    companion object {
        val TAG: String = LogUtil.makeTag(ListSearchViewable::class.java)

    }

}


