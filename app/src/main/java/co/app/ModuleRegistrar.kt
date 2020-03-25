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

package co.app

import android.content.Context
import androidx.collection.ArrayMap
import co.app.common.search.SearchResult
import promise.commons.data.log.LogUtil
import promise.ui.Viewable
import promise.ui.adapter.DiffAdapter
import kotlin.reflect.KClass

class ModuleRegistrar : ModuleRegister {
    override fun register(app: App) {
        LogUtil.d("Register", "app registered")
    }

    override fun registerSearchViewables(context: Context):
            Pair<Map<Class<out SearchResult>, KClass<out Viewable>>, DiffAdapter.Listener<in SearchResult>> =
        Pair(ArrayMap<Class<out SearchResult>, KClass<out Viewable>>()
            .apply {

            }, object: DiffAdapter.Listener<SearchResult>{
            override fun onClick(t: SearchResult, id: Int) {

            }
        })
}