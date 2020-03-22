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

package co.app.domain.message
import co.app.common.AppUser
import co.app.common.photo.Photo
import co.app.domain.search.Search
import co.app.domain.search.SearchResult
import co.app.domain.search.notNullIsContainedIn

data class ChatMessage(var sender: AppUser, var message: String = "", var sentTime: Long): SearchResult {
    override fun onSearch(search: Search): Boolean =
        search.query.notNullIsContainedIn(sender.userName) &&
                search.query.notNullIsContainedIn(message)

    override fun toString(): String {
        return message
    }

    var photos: List<Photo>? = null
    var chatMessageReply: ChatMessage? = null
    var forwarded: Boolean? = null
    var fromCurrentUser: Boolean = false
}

