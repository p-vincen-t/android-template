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
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.app.R
import co.app.dsl.prepareAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.list_view.*
import promise.commons.data.log.LogUtil
import promise.commons.model.List
import promise.commons.tx.PromiseResult
import promise.ui.Viewable
import promise.ui.adapter.DataSource
import promise.ui.adapter.LoadingViewable
import promise.ui.adapter.PromiseAdapter
import kotlin.reflect.KClass

@ReportMeta
class ListReport<T : Any>(
    private val title: String? = null,
    @StringRes private val titleRs: Int = 0,
    private val map: Map<Class<*>, KClass<out Viewable>>? = null,
    private val listData: List<T>? = null,
    private val dataSource: DataSource<T>? = null,
    private val listener: PromiseAdapter.Listener<T>? = null,
    @OrientationType
    private val listOrientation: Int = RecyclerView.VERTICAL,
    private val layoutType: LayoutType? = null,
    private val dataArgs: Any? = null,
    private val loadingViewable: LoadingViewable? = null,
    private val visibleThreshold: Int = 10,
    private val menuRs: Int = 0,
    private val menuClickListener: Toolbar.OnMenuItemClickListener? = null,
    private var footerParams: FooterParams? = null
) : Report, LayoutContainer {

    var view: View? = null

    lateinit var adapter: PromiseAdapter<T>

    fun updateList(listData: List<T>) {
        adapter.setList(listData)
    }

    override fun bind(reportView: ReportView, view: View) {
        this.view = view
        if (title != null) {
            LogUtil.e(TAG, "title ", title)
            reportView.header = title
        }
        if (menuRs != 0 && menuClickListener != null) {
            reportView.menu = menuRs
            reportView.onMenuItemClickListener = menuClickListener
        }
        adapter = list_recycler_view.prepareAdapter(
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
            args = dataArgs
            if (dataSource != null) {
                withPagination(
                    dataSource,
                    loadingViewable ?: ListReportLoader(),
                    visibleThreshold = visibleThreshold
                )
            }
        }
        if (listData != null) {
            LogUtil.e(TAG, "data ", listData)
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
                }
                , 0,
                visibleThreshold)
        }

        if (footerParams != null) {
            LayoutInflater.from(view.context)
                .inflate(footerParams!!.footerLayout, footer_view, true)
            if (footerParams!!.clickListener != null && footerParams!!.viewIds != null) footerParams!!.viewIds!!.forEach {
                footer_view.findViewById<View>(it).setOnClickListener(footerParams!!.clickListener)
            }
        }
    }

    override fun layout(): Int = R.layout.list_view

    override fun toString(): String = "ListReport(title=$title," +
            "listData=$listData," +
            " dataArgs=$dataArgs)"

    override val containerView: View?
        get() = view

    companion object {
        val TAG: String = LogUtil.makeTag(ListReport::class.java)

        @IntDef(
            RecyclerView.HORIZONTAL,
            RecyclerView.VERTICAL
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class OrientationType
    }


}

class FooterParams(
    @LayoutRes val footerLayout: Int,
    val clickListener: View.OnClickListener? = null,
    val viewIds: Array<Int>? = null
)

sealed class LayoutType

class Linear(@ListReport.Companion.OrientationType val orientation: Int = RecyclerView.VERTICAL) :
    LayoutType()

class Grid(val spanCount: Int) : LayoutType()