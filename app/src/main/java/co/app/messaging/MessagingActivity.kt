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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import co.app.BaseActivity
import co.app.R
import kotlinx.android.synthetic.main.activity_messaging.*
import org.jetbrains.anko.intentFor

class MessagingActivity : BaseActivity() {

    private lateinit var messagingViewModel: MessagingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        addBackButton()
        messagingViewModel = ViewModelProvider(this)[MessagingViewModel::class.java]
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        bindService(
            Intent(this, ChatMessageService::class.java),
            messagingViewModel.serviceConnection, Context.BIND_AUTO_CREATE
        )

        fab.setOnClickListener {
            startActivity(intentFor<ChatActivity>())
        }
    }

}
