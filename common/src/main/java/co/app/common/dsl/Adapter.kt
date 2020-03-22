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

package co.app.common.dsl

import androidx.collection.ArrayMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import promise.ui.adapter.DiffAdapter
import promise.ui.adapter.PromiseAdapter
import promise.ui.Viewable
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
    listener: DiffAdapter.Listener<T>,
    viewableClasses: Map<Class<*>, KClass<out Viewable>> = ArrayMap(),
    options: DiffAdapter<T>.() -> Unit = {}
): DiffAdapter<T> {
    val ad = DiffAdapter(
        viewableClasses,
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
    listener: DiffAdapter.Listener<T>,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = true,
    layoutManager: LinearLayoutManager = LinearLayoutManager(
        context,
        orientation,
        reverseLayout
    ),
    viewableClasses: Map<Class<*>, KClass<out Viewable>> = ArrayMap(),
    options: DiffAdapter<T>.() -> Unit = {}
): DiffAdapter<T> {

    this.layoutManager = layoutManager
    val adapter =
        diffAdapter(options = options, viewableClasses = viewableClasses, listener = listener)
    this.adapter = adapter
    return adapter
}