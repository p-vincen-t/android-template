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

import android.view.View
import co.app.R
import co.app.domain.message.ChatThread
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_thread.*
import promise.ui.Viewable

class ChatThreadViewable(private val chatThread: ChatThread) : Viewable, LayoutContainer {

    lateinit var view: View

    override fun layout(): Int = R.layout.chat_thread

    override fun bind(view: View?, args: Any?) {
        message_text_view.text = chatThread.lastChatMessage.message
        username_text_view.text = chatThread.user.userName
        description_text_view.text = chatThread.productOrServiceDescription
        profile_photo_photo_view.setPhoto(chatThread.user.photo!!)
        time_text_view.text = chatThread.lastChatMessage.sentTimeString
    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}