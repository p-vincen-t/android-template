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

package co.app.request.product

import android.content.Context
import android.view.View
import co.app.dsl.Grid
import co.app.dsl.startActivity
import co.app.report.*
import co.app.request.R
import co.app.request.domain.product.Product
import co.app.request.domain.product.ProductsRepository
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.product.*
import promise.ui.adapter.DataSource
import promise.ui.adapter.PromiseAdapter
import java.lang.ref.WeakReference
import promise.commons.model.List as PromiseList

@ReportMeta
class ProductReport(private val product: Product,
                    private val productsRepository: ProductsRepository,
                    private val context: WeakReference<Context>,
                    private val args: Any?): Report, LayoutContainer {
    lateinit var view: View
    override fun bind(reportView: ReportView, view: View) {
        this.view = view
        category_text_view.text = product.category
        skus_report.report = ListReport(
            listener = object : PromiseAdapter.Listener<ProductSKUViewHolder> {
                override fun onClick(t: ProductSKUViewHolder, id: Int) {
                    context.get()?.let {
                        it.startActivity<ProductDetailsActivity> {
                            putExtra(
                                ProductDetailsActivity.SKU_ID,
                                t.productSKU.id
                            )
                        }
                    }
                }
            },
            dataArgs = args,
            layoutType = Grid(3),
            dataSource = DataSource<ProductSKUViewHolder> { response,
                                                            skip,
                                                            take ->
                productsRepository.getProductSKUs(
                    product,
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
        )
    }

    override fun layout(): Int = R.layout.product

    override val containerView: View?
        get() = view

}