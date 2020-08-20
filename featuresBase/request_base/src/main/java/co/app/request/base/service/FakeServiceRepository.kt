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

package co.app.request.base.service

import android.content.Context
import android.util.ArrayMap
import androidx.lifecycle.LiveData
import co.app.common.ID
import co.app.common.Resource
import co.app.common.account.AppUser
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.request.base.R
import co.app.request.domain.service.Service
import co.app.request.domain.service.ServicesRepository
import promise.commons.tx.AsyncEither
import promise.commons.tx.Either
import java.lang.ref.WeakReference
import promise.commons.model.List as PromiseList

class FakeServiceRepository : ServicesRepository {
    override val productsLiveData: LiveData<List<Service>>
        get() = TODO("Not yet implemented")

    override fun getServices(): Either<Resource<List<Service?>>> {
        TODO("Not yet implemented")
    }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): AsyncEither<Pair<Pair<String, Int>, List<SearchResult>>> {
        return AsyncEither { resolve, reject ->
            val user = AppUser(
                ID.generate(),
                "userna",
                null
            )
            val services = PromiseList.generate(3) {
                Service(user, "category", "name", "desc", true)
            }
            resolve(Pair(Pair("request", R.string.services), services))
        }
    }
}