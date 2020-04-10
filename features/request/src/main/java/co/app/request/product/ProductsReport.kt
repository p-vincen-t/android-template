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
import co.app.report.Report
import co.app.report.ReportMeta
import co.app.report.ReportView
import co.app.request.domain.product.Product
import com.nesst.request.R
import kotlinx.android.extensions.LayoutContainer
import promise.commons.model.List

@ReportMeta(
    headerRes = R.string.products,
    menu = R.menu.products
)
class ProductsReport(private val products: List<Product>, private val args: Any?) : Report, LayoutContainer {

    lateinit var view: View
    override fun bind(reportView: ReportView, view: View) {
        this.view = view

    }

    override fun layout(): Int = R.layout.product

    override val containerView: View?
        get() = view
}