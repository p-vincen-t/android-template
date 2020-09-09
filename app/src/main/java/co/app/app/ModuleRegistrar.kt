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

package co.app.app

import android.content.Context
import androidx.collection.ArrayMap
import co.app.App
import co.app.ModuleRegister
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.domain.message.ChatMessage
import co.app.messaging.chat.ChatMessageViewable
import co.app.report.ListReport
import co.app.report.Report
import co.app.search.SearchResultsViewHolder
import promise.commons.data.log.LogUtil
import promise.ui.Viewable
import java.lang.ref.WeakReference
import kotlin.reflect.KClass
import promise.commons.model.List as PromiseList

class ModuleRegistrar : ModuleRegister() {
    override fun onRegister(app: App) {
        LogUtil.d(TAG, "app registering")
        app.initComponents()
        //registerSearchableRepository(app.reposComponent().messageRepository())
    }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): List<SearchResultsViewHolder> {
        val searchResultViewables = ArrayList<SearchResultsViewHolder>()
        val chatMessagesViewable = SearchResultsViewHolder(
            promise.commons.model.List.generate(3) {
                ChatMessageViewable(ChatMessage.STUB.stub())
            })
        searchResultViewables.add(chatMessagesViewable)

        return searchResultViewables
    }

    override fun onRegisterSearchableViews(context: WeakReference<Context>): Pair<String, (Pair<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit>? =
        Pair("app", { results, args, resolve ->
            if (results.first == R.string.messages) {
                val map = ArrayMap<Class<*>, KClass<out Viewable>>().apply {
                    put(
                        ChatMessage::class.java,
                        ChatMessageViewable::class
                    )
                }
                resolve(
                    ListReport<ChatMessage>(
                        listData = PromiseList(results.second).map { it as ChatMessage },
                        map = map,
                        dataArgs = args
                    )
                )
            }
        })
}