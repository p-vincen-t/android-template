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

import co.app.common.Acceptor
import co.app.common.Visitor
import co.app.common.account.AppUser
import co.app.common.search.SearchResult
import com.google.auto.value.AutoValue
import promise.commons.model.Identifiable

@AutoValue
abstract class Product : Identifiable<Int>, Acceptor<Product, Any> {
    abstract val registrar: AppUser
    abstract val category: CharSequence
    abstract val name: CharSequence
    abstract val description: CharSequence
    abstract val active: Boolean

    override fun accept(t: Visitor<Product, Any>): Any = t.visit(this)

    override fun getId(): Int {
        TODO("Not yet implemented")
    }

    override fun setId(t: Int) {
        TODO("Not yet implemented")
    }
}
