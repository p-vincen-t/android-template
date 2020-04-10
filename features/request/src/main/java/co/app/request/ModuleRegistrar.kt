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
import android.util.Log
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import co.app.App
import co.app.ModuleRegister
import co.app.common.search.SearchRepository
import co.app.common.search.SearchResult
import co.app.dsl.startActivity
import co.app.report.Linear
import co.app.report.ListReport
import co.app.report.Report
import co.app.request.base.product.FakeProductsRepositoryImpl
import co.app.request.domain.product.Product
import co.app.request.domain.product.ProductsRepository
import co.app.request.domain.service.Service
import co.app.request.product.ProductSKUDetailActivity
import co.app.request.product.ProductSKUViewHolder
import co.app.request.service.ServiceDetailsActivity
import co.app.request.service.ServiceViewHolder
import com.nesst.request.R
import promise.commons.data.log.LogUtil
import promise.ui.Viewable
import promise.ui.adapter.DataSource
import promise.ui.adapter.PromiseAdapter
import java.lang.ref.WeakReference
import kotlin.reflect.KClass
import promise.commons.model.List as PromiseList

class ModuleRegistrar : ModuleRegister {

    lateinit var productsRepository: ProductsRepository

    override fun onRegister(app: App) {
        productsRepository = FakeProductsRepositoryImpl()
        SearchRepository.registerSearchableRepository(productsRepository)
    }

    override fun onRegisterSearchableViews(context: WeakReference<Context>): Pair<String, (Map<Int, List<SearchResult>>, Any?, (Report) -> Unit) -> Unit>? =
        "request" to { results, args, resolve ->
            LogUtil.e("_'moduleRegister", results)
            results.forEach { entry ->
                when (entry.key) {
                    R.string.products -> resolve(ListReport(
                        title = context.get()?.getString(R.string.products),
                        listener = object : PromiseAdapter.Listener<ProductSKUViewHolder> {
                            override fun onClick(t: ProductSKUViewHolder, id: Int) {
                                context.get()?.let {
                                    it.startActivity<ProductSKUDetailActivity> {
                                        putExtra(
                                            ProductSKUDetailActivity.SKU_ID,
                                            t.productSKU.id
                                        )
                                    }
                                }
                            }
                        },
                        dataArgs = args,
                        layoutType = Linear(orientation = RecyclerView.HORIZONTAL),
                        dataSource = DataSource<ProductSKUViewHolder> { response,
                                                                        skip,
                                                                        take ->
                            productsRepository.getProductSKUs(
                                entry.value.first() as Product,
                                skip,
                                take
                            ).foldOnUI({ skus ->
                                response.response(PromiseList(skus!!).map {
                                    ProductSKUViewHolder(
                                        it
                                    )
                                })
                            }, {
                                response.run { error(it) }
                            })
                        }
                    ))
                    else -> resolve(
                        ListReport<Service>(
                            title = context.get()?.getString(entry.key),
                            listData = PromiseList(entry.value).map { it as Service },
                            map = ArrayMap<Class<*>, KClass<out Viewable>>().apply {
                                put(Service::class.java, ServiceViewHolder::class)
                            },
                            listener = object : PromiseAdapter.Listener<Service> {
                                override fun onClick(t: Service, id: Int) {
                                    context.get()?.let {
                                        it.startActivity<ServiceDetailsActivity> {
                                            putExtra(ServiceDetailsActivity.SERVICE_ID, t.uId)
                                        }
                                    }
                                }
                            },
                            dataArgs = args
                        )
                    )
                }
            }
        }

}