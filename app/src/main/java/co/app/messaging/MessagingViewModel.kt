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

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.common.BaseViewModel
import co.app.domain.message.ChatThread
import co.app.domain.message.MessageRepository
import co.app.domain.message.MessagesError
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import promise.commons.model.List
import promise.commons.tx.PromiseResult

class MessagingViewModel(private val messageService: MessageRepository,
                         private val promise:AndroidPromise) : BaseViewModel() {

    private val _threads: MutableLiveData<List<ChatThread>> = MutableLiveData()

    fun threads(): LiveData<List<ChatThread>> = _threads

    fun loadThreads() {
        pageThreads(PromiseResult<List<ChatThread>, Throwable>()
            .withCallback {
                _threads.value = it
            }
            .withErrorCallback {

            }, 0, 20)
    }

    fun pageThreads(response: PromiseResult<List<ChatThread>, *>, skip: Int, take: Int) {
        promise.execute( {
            messageService.getMessageThreads(skip, take, PromiseResult<kotlin.collections.List<ChatThread>, MessagesError>()
                .withCallback {
                    LogUtil.d(TAG, "threads", it)
                    promise.executeOnUi {
                        response.response(List(it))
                    }
                }
                .withErrorCallback {

                })
        }, 500)
    }

    companion object {
        val TAG : String = LogUtil.makeTag(MessagingViewModel::class.java)
    }
}