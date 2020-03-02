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
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import kotlin.reflect.KClass

fun <T : Any> adapter(
    viewableClasses: Map<Class<*>, KClass<out Viewable>> = ArrayMap(),
    listener: PromiseAdapter.Listener<T>? = null,
    options: PromiseAdapter<T>.() -> Unit = {}
): PromiseAdapter<T> {
    val ad = PromiseAdapter(viewableClasses, listener, null)
    options.invoke(ad)
    return ad
}