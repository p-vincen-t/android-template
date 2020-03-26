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

package co.base.message

import co.app.common.AppUser
import co.app.common.ID
import co.app.common.Photo
import co.app.common.search.Search
import co.app.domain.message.ChatMessage
import org.apache.commons.cli.MissingArgumentException
import promise.commons.model.List
import promise.model.AbstractAsyncIDataStore
import java.util.*

const val SKIP_ARG = "skip_arg"
const val TAKE_ARG = "take_arg"
const val CHAT_THREAD = "chat_thread"
const val SEARCH_ARG = "search_arg"

class AsyncMessageRepo constructor(
    private val chatMessageRecordDao: ChatMessageRecordDao,
    private val chatUserDao: ChatUserDao
) : AbstractAsyncIDataStore<ChatMessage>() {
    @Throws(MissingArgumentException::class)
    override fun findAll(
        res: (List<out ChatMessage>?) -> Unit,
        err: ((Exception) -> Unit)?,
        args: Map<String, Any?>?
    ) {
        when {
            args == null -> {
                res(List(chatMessageRecordDao.getDistinctMessages().value!!.map {
                    it.toChatMessage(chatUserDao)
                }))
            }
            args.containsKey(SEARCH_ARG) -> {
                val search = args[SEARCH_ARG] as Search
                if (search.query.endsWith("k")) {
                    res(List())
                    return
                }
                val appUser = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username",
                    Photo()
                )
                val appUser2 = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username2",
                    Photo()
                )

                //res(List(messages), null)
                res(List.generate(30) {
                    ChatMessage(if (it % 2 == 0) appUser else appUser2, "message", Date().time)
                })
                /*res(List<ChatMessage>(chatMessageRecordDao.searchMessages(args[SEARCH_ARG].toString())
                    .map {
                        it.toChatMessage(chatUserDao)
                    }), null
                )*/
            }
            args.containsKey(SKIP_ARG) && args.containsKey(TAKE_ARG) -> {
               /* val messages = chatMessageRecordDao.getDistinctMessages().value!!.map {
                    it.toChatMessage(chatUserDao)
                }*/
                val appUser = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username",
                    Photo()
                )
                val appUser2 = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username2",
                    Photo()
                )

                //res(List(messages), null)
               res(List.generate(args[TAKE_ARG] as Int) {
                   ChatMessage(if (it % 2 == 0) appUser else appUser2, "message", Date().time)
               })

            }

            args.containsKey(SKIP_ARG) && args.containsKey(TAKE_ARG)  && args.containsKey(
                CHAT_THREAD)-> {
                /* val messages = chatMessageRecordDao.getDistinctMessages().value!!.map {
                     it.toChatMessage(chatUserDao)
                 }*/
                val appUser = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username",
                    Photo()
                )
                val appUser2 = AppUser(
                    ID.from(UUID.randomUUID().toString()), "username2",
                    Photo()
                )

                //res(List(messages), null)
                res(List.generate(args[TAKE_ARG] as Int) {
                    ChatMessage(if (it % 2 == 0) appUser else appUser2, "message", Date().time)
                })

            }
        }
    }

}
