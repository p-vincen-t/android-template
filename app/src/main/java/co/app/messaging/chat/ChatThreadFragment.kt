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

package co.app.messaging.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import co.app.BaseFragment
import co.app.R
import co.app.dsl.prepareAdapter
import co.app.dsl.startActivity
import co.app.domain.message.ChatThread
import kotlinx.android.synthetic.main.fragment_chat_thread_list.*
import promise.commons.data.log.LogUtil
import promise.ui.Viewable
import promise.ui.adapter.PromiseAdapter
import javax.inject.Inject
import kotlin.reflect.KClass

class ChatThreadFragment : BaseFragment(), PromiseAdapter.Listener<ChatThread> {

    private lateinit var threadsViewModel: ThreadsViewModel

    @Inject
    lateinit var messagingViewModelFactory: ChatViewModelFactory

    private lateinit var adapter: PromiseAdapter<ChatThread>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_thread_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaggerChatComponent.builder()
            .reposComponent(app.reposComponent)
            .build()
            .inject(this)

        threadsViewModel = ViewModelProvider(
            this,
            messagingViewModelFactory
        )[ThreadsViewModel::class.java]
        adapter =
            chat_threads_recycler_view.prepareAdapter(
                viewableClasses = ArrayMap<Class<*>, KClass<out Viewable>>()
                    .apply {
                        put(
                            ChatThread::class.java,
                            ChatThreadViewable::class
                        )
                    },
                listener = this
            ) {
                args = true
            }
        prepareUi()
    }

    private fun prepareUi() {
        threadsViewModel.threads().observe(viewLifecycleOwner, Observer {
            LogUtil.d(TAG, "threads", it)
            loading_layout.showContent()
            adapter.add(promise.commons.model.List(it))
        })

        loading_layout.showLoading(null)
        threadsViewModel.loadThreads()

    }

    override fun onClick(t: ChatThread, id: Int) = context!!.startActivity<ChatActivity> {
        putExtra(ChatActivity.THREAD_ID, t.id)
    }

    companion object {
        val TAG: String = LogUtil.makeTag(ChatThreadFragment::class.java)
    }

}
