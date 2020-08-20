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
import io.bloco.faker.Faker
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
            Thread.sleep(1000)
            val faker = Faker()
            resolve(PromiseList.generate(5) {
                ProductSKU(product).apply {
                    name = faker.commerce.productName()
                    description = faker.company.industry()
                }
            })
        }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): AsyncEither<Pair<Pair<String, Int>, List<SearchResult>>> = AsyncEither { resolve, _ ->
        val faker = Faker()
        val products = PromiseList.generate(3) {
            val user = AppUser(
                ID.generate(),
                "userna",
                null
            )
            Product(
                user,
                faker.commerce.productName(),
                faker.commerce.productName(),
                faker.commerce.material(),
                true
            )
        }
        resolve(Pair(Pair("request", R.string.products), products))
    }
}