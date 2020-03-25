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
import co.app.domain.message.ChatMessage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.recipient_chat.*
import kotlinx.android.synthetic.main.sender_chat.*
import promise.ui.Viewable

class SenderMessageViewable constructor(private val chatMessage: ChatMessage) : Viewable,
    LayoutContainer {
    lateinit var view: View
    override fun layout(): Int = R.layout.sender_chat

    override fun bind(view: View?, args: Any?) {
        sender_time_text_view.text = chatMessage.sentTimeString
        message_body.text = chatMessage.message
    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}

class RecipientMessageViewable constructor(
    private val chatMessage: ChatMessage
) : Viewable, LayoutContainer {

    lateinit var view: View

    override fun layout(): Int = R.layout.recipient_chat

    override fun bind(view: View?, args: Any?) {
        time_text_view.text = chatMessage.sentTimeString
        message_body_text_view.text = chatMessage.message

    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}


class ChatMessageViewable(chatMessage: ChatMessage) : Viewable {
    private val viewable: Viewable =
        if (chatMessage.fromCurrentUser) SenderMessageViewable(chatMessage)
    else RecipientMessageViewable(chatMessage)

    override fun layout(): Int = viewable.layout()
    override fun bind(view: View?, args: Any?) = viewable.bind(view, args)
    override fun init(view: View) = viewable.init(view)
}