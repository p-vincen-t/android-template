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

import android.annotation.SuppressLint
import co.app.common.ID
import co.app.common.account.AppUser
import co.app.common.account.AppUserImpl
import co.app.common.search.Search
import co.app.request.domain.product.Product
import promise.database.Entity
import promise.database.HasOne
import promise.database.Number
import promise.database.Text
import promise.db.ActiveRecord

@SuppressLint("ParcelCreator")
@Entity(tableName = "products")
class ProductImpl : ActiveRecord<ProductImpl>(), Product {

    @Text(columnName = "name")
    var productName: String = ""

    @Text(columnName = "category")
    var productCategory: String = ""

    @Number(columnName = "active")
    var productActive: Boolean = true

    @Text(columnName = "description")
    var productDescription: String = ""

    @Text(columnName = "uid")
    var userId: ID? = null

    @HasOne
    var appUserImpl: AppUserImpl? = null

    override val registrar: AppUser
        get() = appUserImpl ?: throw IllegalStateException("Product not registered")

    override val category: CharSequence
        get() = productCategory
    override val name: CharSequence
        get() = productName
    override val description: CharSequence
        get() = productDescription
    override val active: Boolean
        get() = productActive

    override fun getId(): Int = super.getId()

    override fun setId(t: Int) {
        super.setId(t)
    }

    override fun onSearch(search: Search): Boolean {
        return appUserImpl?.onSearch(search) ?: false
    }

    override fun getEntity(): ProductImpl = this
}