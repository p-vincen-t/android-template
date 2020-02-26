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

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.BaseViewModel
import co.app.messaging.ChatMessageService.LocalBinder

class MessagingViewModel constructor(messageService: ChatMessageService) : BaseViewModel() {

    private val binder = MutableLiveData<ChatMessageService>()

    fun serviceBinder(): LiveData<ChatMessageService> = binder

    val serviceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serviceBinder = service as LocalBinder
                binder.value =  serviceBinder.service
            }
        }
    }
}