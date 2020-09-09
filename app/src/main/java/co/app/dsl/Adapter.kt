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

package co.app.dsl

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter
import promise.ui.adapter.PromiseAdapter
import kotlin.reflect.KClass

fun <T : Any> adapter(
    viewableClasses: Map<Class<*>, KClass<out Viewable>>? = null,
    listener: PromiseAdapter.Listener<T>? = null,
    options: PromiseAdapter<T>.() -> Unit = {}
): PromiseAdapter<T> {
    val ad = if (viewableClasses != null) PromiseAdapter(
        viewableClasses,
        listener, null
    ) else PromiseAdapter(listener, null)
    options.invoke(ad)
    return ad
}

fun <T : Any> diffAdapter(
    listener: DiffAdapter.Listener<T>? = null,
    viewableClasses: Map<Class<*>, KClass<out Viewable>>? = null,
    options: DiffAdapter<T>.() -> Unit = {}
): DiffAdapter<T> {
    val ad = DiffAdapter(
        viewableClasses ?: ArrayMap(),
        listener,
        null
    )
    options.invoke(ad)
    return ad
}

fun <T : Any> RecyclerView.prepareAdapter(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = true,
    layoutManager: LinearLayoutManager = LinearLayoutManager(
        context,
        orientation,
        reverseLayout
    ),
    viewableClasses: Map<Class<*>, KClass<out Viewable>>? = null,
    listener: PromiseAdapter.Listener<T>? = null,
    options: PromiseAdapter<T>.() -> Unit = {}
): PromiseAdapter<T> {

    this.layoutManager = layoutManager
    val adapter = adapter(options = options, viewableClasses = viewableClasses, listener = listener)
    this.adapter = adapter
    return adapter
}

fun <T : Any> RecyclerView.prepareListAdapter(
    listener: DiffAdapter.Listener<T>? = null,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    layoutManager: LinearLayoutManager = LinearLayoutManager(
        context,
        orientation,
        reverseLayout
    ),
    viewableClasses: Map<Class<*>, KClass<out Viewable>>? = null,
    options: DiffAdapter<T>.() -> Unit = {}
): DiffAdapter<T> {
    this.layoutManager = layoutManager
    val adapter =
        diffAdapter(options = options, viewableClasses = viewableClasses, listener = listener)
    this.adapter = adapter
    return adapter
}

fun <I> RecyclerView.listItems(
    items: List<I>,
    @LayoutRes itemLayout: Int,
    layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
    bind: (View, I, Int) -> Unit,
    itemClick: (I, Int) -> Unit = { _, _ -> }
) {
    this.layoutManager = layoutManager
    this.adapter = BaseAdapter(items, itemLayout, bind, itemClick)
}


class BaseAdapter<I>(
    private val items: List<I>,
    @LayoutRes private val lyt: Int,
    private val bind: (View, I, Int) -> Unit,
    private val itemClick: (I, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(parent inflate lyt)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = items[position]
        bind(holder.itemView, item, position)
        holder.itemView.setOnClickListener { itemClick(item, position) }
    }

    class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}