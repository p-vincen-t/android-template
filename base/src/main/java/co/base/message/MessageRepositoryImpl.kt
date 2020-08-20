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

import android.content.Context
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.common.ID
import co.app.common.account.AppUser
import co.app.common.photo.Photo
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.domain.message.*
import co.base.R
import co.base.RepoScope
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormat
import promise.commons.data.log.LogUtil
import promise.commons.tx.AsyncEither
import promise.commons.tx.Either
import promise.commons.tx.Right
import promise.model.Repository
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@RepoScope
class MessageRepositoryImpl @Inject constructor(
    private val chatDatabase: ChatDatabase,
    private val repository: Repository<ChatMessage>
) :
    MessageRepository {

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): AsyncEither<Pair<Pair<String, Int>, List<SearchResult>>> =
        AsyncEither { resolve, _ ->
            LogUtil.e("messages", "searching")
            val pair = Pair(Pair("app", R.string.messages), chatDatabase.search(search))
            resolve(pair)
        }

    private val chatThreadId: ID = ID.generate()

    companion object {
        val TAG: String = LogUtil.makeTag(MessageRepositoryImpl::class.java)
    }

    private val repoMessages: MutableLiveData<List<ChatMessage>> = MutableLiveData()

    private val repoThreads: MutableLiveData<List<ChatThread>> = MutableLiveData()

    override val messages: LiveData<List<ChatMessage>>
        get() = repoMessages
    override val chatThreads: LiveData<List<ChatThread>>
        get() = repoThreads

    /**
     * called on ui
     */
    override fun getChatThreads(skip: Int, take: Int): Either<Any> =
        AsyncEither { resolve, _ ->
            resolve(Any())
            repoThreads.postValue(chatDatabase.getMessageThreads())
        }

    override fun getChatMessages(
        chatThread: ChatThread,
        skip: Int,
        take: Int
    ): Either<Any> = AsyncEither { resolve, _ ->
        repository.findAll(ArrayMap<String, Any>().apply {
            put(SKIP_ARG, skip)
            put(TAKE_ARG, take)
            put(CHAT_THREAD, chatThread)
        }, { data ->
            repoMessages.postValue(data!!.mapIndexed { i, chatMessage ->
                chatMessage.apply {
                    val d1 = DateTime()
                    val d2 = DateTime(chatMessage.sentTime)
                    val diffInMillis = d1.millis - d2.millis
                    val date = PeriodFormat.getDefault().print(Period(diffInMillis))
                    sentTimeString = date
                    fromCurrentUser = i % 2 == 0
                    message =
                        if (i % 2 == 0) " I sent this long ago" else "You sent this too soon"
                }
            })
            resolve(Any())
        })
    }

    override fun getMessageChatThreads(
        skip: Int,
        take: Int
    ): Either<List<ChatThread>> = AsyncEither { resolve, reject ->
        repository.findAll(ArrayMap<String, Any>().apply {
            put(SKIP_ARG, skip)
            put(TAKE_ARG, take)
        }, { messages ->
            resolve(messages!!.map {
                    it.apply {
                        val d1 = DateTime()
                        val d2 = DateTime(it.sentTime)
                        val diffInMillis = d1.millis - d2.millis
                        val date = PeriodFormat.getDefault().print(Period(diffInMillis))
                        sentTimeString = date
                    }
                }
                .groupBy {
                    it.sender
                }.map {
                    val message = it.list().last()!!
                    ChatThread(
                        chatThreadId,
                        it.name(),
                        "product",
                        message
                    )
                })
        }, {
            reject(MessagesError(it))
        })
    }

    override fun getChatThread(id: ID): Either<ChatThread> {
        LogUtil.d(TAG, "called get thread")
        val appUser = AppUser(
            ID.from(UUID.randomUUID().toString()), "username",
            Photo()
        )
        return Right(
            ChatThread(
                chatThreadId, appUser,
                "product",
                ChatMessage(appUser, "message", Date().time)
            )
        )
    }

    override fun getMessages(
        chatThread: ChatThread,
        skip: Int,
        take: Int
    ): Either<List<ChatMessage>> = AsyncEither { resolve, _ ->
        repository.findAll(ArrayMap<String, Any>().apply {
            put(SKIP_ARG, skip)
            put(TAKE_ARG, take)
            put(CHAT_THREAD, chatThread)
        }, { messages ->
            resolve(messages!!.mapIndexed { i, chatMessage ->
                chatMessage.apply {
                    val d1 = DateTime()
                    val d2 = DateTime(chatMessage.sentTime)
                    val diffInMillis = d1.millis - d2.millis
                    val date = PeriodFormat.getDefault().print(Period(diffInMillis))
                    sentTimeString = date
                    fromCurrentUser = i % 2 == 0
                    message = if (i % 2 == 0) " I sent this long ago" else "You sent this too soon"
                }
            })
        })
    }

    override fun sendMessage(chatMessage: ChatMessage): Either<Int> =
        AsyncEither { resolve, _ ->
            val messages = repoMessages.value!!.toMutableList()
            messages.add(chatMessage)
            repoMessages.postValue(messages)
            resolve(SENT)
        }

}