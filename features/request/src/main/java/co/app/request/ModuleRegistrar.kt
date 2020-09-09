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

package co.app.request

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import co.app.App
import co.app.ModuleRegister
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.dsl.startActivity
import co.app.dsl.Linear
import co.app.report.ListReport
import co.app.report.ListSearchViewable
import co.app.report.Report
import co.app.request.base.product.FakeProductsRepositoryImpl
import co.app.request.base.service.FakeServiceRepository
import co.app.request.domain.product.Product
import co.app.request.domain.product.ProductsRepository
import co.app.request.domain.service.Service
import co.app.request.domain.service.ServicesRepository
import co.app.request.product.ProductReport
import co.app.request.service.ServiceDetailsActivity
import co.app.request.service.ServiceViewHolder
import co.app.search.SearchResultsViewHolder
import promise.commons.data.log.LogUtil
import promise.ui.adapter.PromiseAdapter
import java.lang.ref.WeakReference
import promise.commons.model.List as PromiseList

class ModuleRegistrar : ModuleRegister() {

    lateinit var productsRepository: ProductsRepository
    lateinit var servicesRepository: ServicesRepository

    override fun onRegister(app: App) {
        productsRepository = FakeProductsRepositoryImpl()
        servicesRepository = FakeServiceRepository()
        SearchRepository.registerSearchableRepository(productsRepository)
        SearchRepository.registerSearchableRepository(servicesRepository)
    }

    override fun onSearch(
        context: WeakReference<Context>,
        search: Search
    ): List<SearchResultsViewHolder> {

        val searchResultViewables = ArrayList<SearchResultsViewHolder>()
        val chatMessagesViewable = SearchResultsViewHolder(
            promise.commons.model.List.generate(3) {
                ListSearchViewable<Product>(
                    title = "Category",
                    listData = promise.commons.model.List.generate(3) {
                        Product.
                    }

                )
            })
        searchResultViewables.add(chatMessagesViewable)

        return searchResultViewables
    }

    override fun onRegisterSearchableViews(context: WeakReference<Context>): Pair<String, (Pair<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit>? =
        "request" to { results, args, resolve ->
            LogUtil.e("_'moduleRegister", results)
            if (results.first == R.string.products) {
                val categories = PromiseList(
                    results.second
                ).groupBy { (it as Product) }
                categories.forEach {
                    resolve(
                        ProductReport(
                            it.name(),
                            productsRepository,
                            context,
                            args
                        )
                    )
                }
            }

            if (results.first == R.string.services) resolve(
                ListReport<ServiceViewHolder>(
                    title = context.get()?.getString(R.string.services),
                    listData = PromiseList(results.second)
                        .map { ServiceViewHolder(it as Service) },
                    layoutType = Linear(orientation = RecyclerView.HORIZONTAL),
                    listener = object : PromiseAdapter.Listener<ServiceViewHolder> {
                        override fun onClick(t: ServiceViewHolder, id: Int) {
                            context.get()?.let {
                                it.startActivity<ServiceDetailsActivity> {
                                    putExtra(ServiceDetailsActivity.SERVICE_ID, t.service.uId)
                                }
                            }
                        }
                    },
                    dataArgs = args
                )
            )
        }
}