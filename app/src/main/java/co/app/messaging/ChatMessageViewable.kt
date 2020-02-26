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

import android.view.View
import co.app.R
import co.app.domain.message.ChatMessage
import promise.ui.model.Viewable

class SenderMessageViewable constructor(private val chatMessage: ChatMessage): Viewable {
    override fun layout(): Int = R.layout.sender_chat

    override fun bind(view: View?, args: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init(view: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class RecipientMessageViewable constructor(private val chatMessage: ChatMessage): Viewable {
    override fun layout(): Int = R.layout.recipient_chat

    override fun bind(view: View?, args: Any?) {

    }

    override fun init(view: View?) {

    }
}


class ChatMessageViewable constructor(private val chatMessage: ChatMessage): Viewable {
    private val viewable: Viewable by lazy {
        if (chatMessage.fromCurrentUser) SenderMessageViewable(chatMessage)
        else RecipientMessageViewable(chatMessage)
    }
    override fun layout(): Int = viewable.layout()
    override fun bind(view: View?, args: Any?) = viewable.bind(view, args)
    override fun init(view: View?) = viewable.init(view)
}