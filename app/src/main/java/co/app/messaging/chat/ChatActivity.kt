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
import android.view.View
import androidx.collection.ArrayMap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import co.app.BaseActivity
import co.app.R
import co.app.common.AppUser
import co.app.common.Attachment
import co.app.common.ID
import co.app.dsl.prepareAdapter
import co.app.common.Photo
import co.app.photo.PhotoView
import co.app.databinding.ActivityChatBinding
import co.app.domain.message.ChatMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_chat_thread_list.loading_layout
import promise.ui.Viewable
import promise.ui.adapter.PromiseAdapter
import javax.inject.Inject
import kotlin.reflect.KClass

class PhotoViewable(private val photo: Photo) : Viewable {

    private lateinit var photoView: PhotoView
    override fun layout(): Int = R.layout.chat_attached_file_item

    override fun bind(view: View?, args: Any?) {
        photoView.setPhoto(photo)
    }

    override fun init(view: View) {
        photoView = view as PhotoView
    }
}

class ChatActivity : BaseActivity(), PromiseAdapter.Listener<ChatMessage>, View.OnClickListener {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    lateinit var chatViewModel: ChatViewModel

    var chatService: ChatService? = null

    lateinit var appUser: AppUser

    private lateinit var photosAdapter: PromiseAdapter<Photo>

    lateinit var adapter: PromiseAdapter<ChatMessage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)
        setSupportActionBar(toolbar)
        addBackButton()
        DaggerChatComponent.builder()
            .reposComponent(app.reposComponent())
            .build()
            .inject(this)
        chatViewModel = ViewModelProvider(this, chatViewModelFactory)[ChatViewModel::class.java]
        binding.viewModel = chatViewModel
        app.connectChatService<ChatService>({
            chatService = it
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        adapter =
            chats_recycler_view.prepareAdapter(
                viewableClasses = ArrayMap<Class<*>, KClass<out Viewable>>()
                    .apply {
                        put(
                            ChatMessage::class.java,
                            ChatMessageViewable::class
                        )
                    },
                listener = this
            ) {
                args = true
            }
        loading_layout.showLoading(null)

        chatViewModel.messageRepository.messages.observe(this, Observer {
            loading_layout.showContent()
            adapter.setList(promise.commons.model.List(it))
        })

        if (intent.hasExtra(THREAD_ID)) {
            val id: ID = intent.getParcelableExtra(THREAD_ID)!!
            chatViewModel.messageRepository.getChatThread(id)
                .foldOnUI({
                    appUser = it.user
                    chatViewModel.messageRepository.getChatMessages(it, 0, 10).fold({}, {})
                    username_text_view.text = appUser.userName
                    description_text_view.text = it.productOrServiceDescription
                    account_photo.setPhoto(it.user.photo)
                }, {})
        } else throw IllegalStateException("show only if there's existing messages")
        photosAdapter =
            attached_files_recycler_view.prepareAdapter(
                viewableClasses = ArrayMap<Class<*>, KClass<out Viewable>>()
                    .apply {
                        put(
                            Photo::class.java,
                            PhotoViewable::class
                        )
                    },
                layoutManager = GridLayoutManager(this, 3)
            )

        chatViewModel.messagePhotos().observe(this, Observer {
            if (it.isEmpty()) attached_files_recycler_view.visibility = View.GONE
            else {
                attached_files_recycler_view.visibility = View.VISIBLE
                photosAdapter.add(promise.commons.model.List(it))
            }
        })

        attach_file_image_view.setOnClickListener(this)
        send_image_view.setOnClickListener(this)
    }

    companion object {
        const val THREAD_ID = "thread_id"
    }

    override fun onClick(t: ChatMessage, id: Int) {

    }

    override fun onAttachmentAcquired(attachment: Attachment) {
        chatViewModel.addPhotos(attachment as Photo)
    }

    override fun onClick(v: View) {
        if (v == send_image_view) {
            if (chatService != null) {

            }
        }
        if (v == attach_file_image_view) requestAttachment(ATTACHMENT_TYPE_PHOTO)
    }
}
