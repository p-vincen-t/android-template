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

package co.app.messaging

import android.os.Bundle
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import co.app.BaseActivity
import co.app.R
import co.app.common.dsl.adapter
import co.app.common.dsl.startActivity
import co.app.domain.message.ChatThread
import kotlinx.android.synthetic.main.activity_messaging.*
import promise.commons.data.log.LogUtil
import promise.ui.adapter.PromiseAdapter
import promise.ui.Viewable
import javax.inject.Inject
import kotlin.reflect.KClass

class MessagingActivity : BaseActivity(), PromiseAdapter.Listener<ChatThread> {

    private lateinit var messagingViewModel: MessagingViewModel

    @Inject
    lateinit var messagingViewModelFactory: MessageViewModelFactory

    lateinit var adapter: PromiseAdapter<ChatThread>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        addBackButton()
        DaggerChatMessageServiceComponent.builder()
            .reposComponent(app.reposComponent)
            .build()
            .inject(this)

        messagingViewModel = ViewModelProvider(
            this,
            messagingViewModelFactory
        )[MessagingViewModel::class.java]
        fab.show()
    }

    private fun prepareUi() {
        messagingViewModel.threads().observe(this,  Observer {
            LogUtil.d(TAG, "threads", it)
            adapter.add(it)
        })

        messagingViewModel.loadThreads()

        fab.setOnClickListener {
            startActivity<ChatActivity>()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        adapter = adapter(
            ArrayMap<Class<*>, KClass<out Viewable>>()
                .apply {
                    put(
                        ChatThread::class.java,
                        ChatThreadViewable::class
                    )
                },
            this
        ) {
            args = true
            /*withPagination(
                dataSource = DataSource { response, skip, take ->
                    messagingViewModel.pageThreads(response, skip, take)
                },
                loadingView = AppLoaderViewable("Loading chats"),
                visibleThreshold = 20
            )*/
        }

        threads_recycler_view.layoutManager = LinearLayoutManager(this)
        threads_recycler_view.adapter = adapter
        prepareUi()
    }

    override fun onClick(t: ChatThread, id: Int) {
        startActivity<ChatActivity> {
            putExtra(ChatActivity.THREAD_ID, t.id)
        }
    }

    companion object {
        val TAG: String = LogUtil.makeTag(MessagingActivity::class.java)
    }

}
