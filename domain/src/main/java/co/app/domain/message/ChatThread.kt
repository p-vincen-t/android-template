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

import co.app.common.account.AppUser
import co.app.common.ID
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.common.search.notNullIsContainedIn

/**
 * entails a user with the last message sent
 */
data class ChatThread(
    /**
     * the id of the thread
     */
    var id: ID,
    /**
     * the user who sent the message
     */
    var user: AppUser,
    /**
     * the description of the message, product or service
     */

    var productOrServiceDescription: String,
    /**
     * the last sent message
     */
    var lastChatMessage: ChatMessage
): SearchResult {
    override fun onSearch(search: Search): Boolean =
        search.query.notNullIsContainedIn(user.userName) ||
                lastChatMessage.onSearch(search) ||
                productOrServiceDescription.notNullIsContainedIn(search.query)

    override fun toString(): String {
        return user.toString() + lastChatMessage.toString()
    }
}