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

package co.app.request.domain.product

import co.app.common.ID
import co.app.common.photo.Photo
import co.app.request.domain.Pricing
import co.app.request.domain.Quantity

class ProductSKU(val product: Product) {
    var id: ID? = null
    var name: CharSequence? = null
    var description: CharSequence? = null
    var photos: List<Photo>? = null
    var quantity: Quantity? = null
    var price: Pricing? = null
    var rating: Boolean? = null
    override fun toString(): String {
        return "ProductSKU(product=$product, id=$id, name=$name, description=$description, photos=$photos, quantity=$quantity, price=$price, rating=$rating)"
    }

}