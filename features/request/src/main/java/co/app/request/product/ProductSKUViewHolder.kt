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

import android.view.View
import co.app.common.search.SearchResultViewable
import co.app.request.R
import co.app.request.domain.product.ProductSKU
import kotlinx.android.extensions.LayoutContainer
import promise.commons.data.log.LogUtil
import promise.ui.Viewable

class ProductSKUViewHolder(val productSKU: ProductSKU) : SearchResultViewable(), LayoutContainer {
    lateinit var view: View

    override fun layout(): Int = R.layout.product_sku

    override fun bind(view: View, args: Any?) {
        LogUtil.e("sku tag", "sku ", productSKU.product.name)
    }

    override fun hashCode(): Int {
        return productSKU.hashCode()
    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}