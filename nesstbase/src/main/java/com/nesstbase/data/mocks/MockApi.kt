/*
 * Copyright 2017, Nesst
 * Licensed under the Apache License, Version 2.0, "Nesst Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesstbase.data.mocks

import com.appham.mockinizer.RequestFilter
import okhttp3.mockwebserver.MockResponse
import java.util.concurrent.TimeUnit

val mocks: Map<RequestFilter, MockResponse> = mapOf(

    RequestFilter("/login") to MockResponse().apply {

    },

    RequestFilter("/typicode/demo/mocked") to MockResponse().apply {
        setResponseCode(200)
        setBody(
            """
                        [
                          {
                            "id": 555,
                            "title": "Banana Mock"
                          },
                          {
                            "id": 675,
                            "title": "foooo"
                          }
                        ]
                    """.trimIndent()
        )
        setBodyDelay(1, TimeUnit.SECONDS)
        setHeadersDelay(1, TimeUnit.SECONDS)
    },

    RequestFilter("/typicode/demo/mockedError") to MockResponse().apply {
        setResponseCode(400)
        setHeadersDelay(2, TimeUnit.SECONDS)
        setBodyDelay(2, TimeUnit.SECONDS)
    }

)