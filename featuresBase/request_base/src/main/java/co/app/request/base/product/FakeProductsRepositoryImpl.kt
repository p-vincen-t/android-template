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

package co.app.request.base.product

import android.content.Context
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import co.app.common.ID
import co.app.common.Resource
import co.app.common.account.AppUser
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.request.base.R
import co.app.request.domain.product.Product
import co.app.request.domain.product.ProductSKU
import co.app.request.domain.product.ProductsRepository
import promise.commons.tx.AsyncEither
import promise.commons.tx.Either
import java.lang.ref.WeakReference
import promise.commons.model.List as PromiseList

class FakeProductsRepositoryImpl : ProductsRepository {

    override val productsLiveData: LiveData<List<Product>>
        get() = TODO("Not yet implemented")

    override fun getProducts(): Either<Resource<List<Product?>>> {
        TODO("Not yet implemented")
    }

    override fun getProductSKUs(product: Product, skip: Int, take: Int): Either<List<ProductSKU>> =
        AsyncEither { resolve, _ ->
            Thread.sleep(2000)
            resolve(PromiseList.generate(10) {
                ProductSKU(product).apply {

                }
            })
        }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): Either<Map<Pair<String, Int>, List<SearchResult>>> = AsyncEither { resolve, _ ->
        val products = PromiseList.generate(3) {
            val user = AppUser(
                ID.generate(),
                "userna",
                null
            )
            Product(user, "category $it", "name", "desc", true)
        }
        val map = ArrayMap<Pair<String, Int>, List<SearchResult>>()
        map[Pair("request", R.string.products)] = products
        resolve(map)
    }
}