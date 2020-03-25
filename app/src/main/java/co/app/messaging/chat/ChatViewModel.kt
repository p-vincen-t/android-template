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

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.BaseViewModel
import co.app.common.Photo
import co.app.domain.message.MessageRepository

class ChatViewModel constructor(val messageRepository: MessageRepository) : BaseViewModel() {

    private val _messagePhotos: MutableLiveData<List<Photo>> = MutableLiveData()

    fun messagePhotos(): LiveData<List<Photo>> = _messagePhotos

    @Bindable
    var message: String = ""

    fun addPhotos(photos: Photo) {
        val photos1 = if(_messagePhotos.value == null) ArrayList() else _messagePhotos.value!!.toMutableList()
        photos1.add(photos)
        _messagePhotos.value = photos1
    }

}