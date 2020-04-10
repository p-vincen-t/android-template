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

import co.app.common.account.AppUser
import co.app.common.ID
import co.app.domain.message.ChatDatabase
import co.app.domain.message.ChatMessage
import co.app.domain.message.ChatThread
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import promise.commons.model.List
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class AsyncMessageRepoTest {

    @Mock
    lateinit var appDatabase: ChatDatabase

    lateinit var asyncMessageRepo: AsyncMessageRepo

    private val threads: List<ChatThread> = List.generate(10) {
        val user = AppUser(
            ID.from(UUID.randomUUID().toString()),
            null,
            null
        )
        ChatThread(
            ID.from(UUID.randomUUID().toString()),
            user,
            "desc",
            ChatMessage(user, "message")
        )
    }

    @Before
    fun setUp() {
        asyncMessageRepo = AsyncMessageRepo(appDatabase)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun findAllThreads() {
        Mockito.`when`(appDatabase.getMessageThreads()).thenReturn(threads)
        asyncMessageRepo.findAll({

        }, {

        }, null)
    }
}