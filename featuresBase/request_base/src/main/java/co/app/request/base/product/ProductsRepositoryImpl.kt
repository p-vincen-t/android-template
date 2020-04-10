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
import co.app.common.Resource
import co.app.common.search.Search
import co.app.common.search.SearchResult
import co.app.request.domain.product.*
import promise.commons.tx.Either
import java.lang.ref.WeakReference

class ProductsRepositoryImpl(
    private val productsDatabase: ProductsDatabase,
    private val productsApi: ProductsApi
) : ProductsRepository {

    override val productsLiveData: LiveData<List<Product>>
        get() = TODO("Not yet implemented")

    override fun getProducts(): Either<Resource<List<Product?>>> {
        TODO("Not yet implemented")
    }

    override fun getProductSKUs(product: Product, skip: Int, take: Int): Either<List<ProductSKU>> {
        TODO("Not yet implemented")
    }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): Either<Map<Pair<String, Int>, List<SearchResult>>> {
        TODO("Not yet implemented")
    }
}