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

package co.app.dashboard.recents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import co.app.R
import co.app.common.ID
import co.app.common.account.AppUser
import co.app.common.photo.Photo
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import co.app.dsl.prepareAdapter
import co.app.dsl.startActivity
import co.app.messaging.chat.ChatActivity
import co.app.messaging.chat.ChatThreadViewable
import co.app.report.ListReport
import co.app.report.ReportHolder
import kotlinx.android.synthetic.main.recent_activities_fragment.*
import promise.ui.adapter.PromiseAdapter
import promise.commons.model.List as PromiseList

val messages: PromiseList<ChatThread> = PromiseList.generate(5) {
    val user = AppUser(ID.generate(), "sername", Photo())
    val message = ChatMessage(user)
    ChatThread(
        ID.generate(),
        user,
        message.chatDescription,
        message
    )
}


class RecentActivitiesFragment : Fragment() {

    companion object {
        fun newInstance() = RecentActivitiesFragment()
    }

    private lateinit var viewModel: RecentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recent_activities_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecentsViewModel::class.java)
        val adapter = recent_list_recycler_view.prepareAdapter<ReportHolder>()
        adapter.add(ReportHolder(RecentReport(viewLifecycleOwner)))
        adapter.add(ReportHolder(ListReport(
            title = "Recent chats",
            listData = messages.map { ChatThreadViewable(it) },
            listener = object : PromiseAdapter.Listener<ChatThreadViewable> {
                override fun onClick(t: ChatThreadViewable, id: Int) {
                    requireContext().startActivity<ChatActivity> {
                        putExtra(ChatActivity.THREAD_ID, t.chatThread.id)
                    }
                }
            }
        )))

        /*recent_chat_report.report = ListReport(
            title = "Recent chats",
            listData = messages,
            map = ArrayMap<Class<*>, KClass<out Viewable>>().apply {
                put(ChatThread::class.java, ChatThreadViewable::class)
            },
            listener = object : PromiseAdapter.Listener<ChatThread> {
                override fun onClick(t: ChatThread, id: Int) {
                    requireContext().startActivity<ChatActivity> {
                        putExtra(ChatActivity.THREAD_ID, t.id)
                    }
                }
            }
        )*/
        /*adapter.add(ReportHolder(ListReport(
            title = "Recent chats",
            listData = messages,
            map = ArrayMap<Class<*>, KClass<out Viewable>>().apply {
                put(ChatThread::class.java, ChatThreadViewable::class)
            },
            listener = object : PromiseAdapter.Listener<ChatThread> {
                override fun onClick(t: ChatThread, id: Int) {
                    requireContext().startActivity<ChatActivity> {
                        putExtra(ChatActivity.THREAD_ID, t.id)
                    }
                }
            }
        )))*/

    }

}
