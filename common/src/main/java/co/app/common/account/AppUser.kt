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

package co.app.common.account

import co.app.common.ID
import co.app.common.photo.Photo
import co.app.common.search.Search
import co.app.common.search.SearchResult
import java.util.*

class AppUser(var userId: ID, var userName: String?, var photo: Photo?):
    SearchResult {
    override fun toString(): String = userName!!

    override fun onSearch(search: Search): Boolean =
        userName!!.toLowerCase(Locale.getDefault()).contains(search.query.toLowerCase(Locale.getDefault()))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AppUser
        if (userId != other.userId) return false
        if (userName != other.userName) return false
        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        return result
    }


}