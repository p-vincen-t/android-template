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
import co.app.common.account.AppUser
import co.app.common.photo.Photo
import co.app.common.search.Search
import co.app.common.common.PhotoRecord
import promise.commons.model.Identifiable
import promise.database.Entity
import promise.database.HasOne
import promise.database.PrimaryKeyAutoIncrement

@Entity(tableName = "app_users")
class AppUserImpl : AppUser(), Identifiable<Int>{

    @HasOne
    var photoRecord: PhotoRecord? = null

    @PrimaryKeyAutoIncrement
    var uid: Int = 0
    override fun getId(): Int {
        return uid
    }

    override fun setId(t: Int) {
        uid = t
    }

    override var userId: ID
        get() = TODO("Not yet implemented")
        set(value) {}
    override var userName: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var photo: Photo?
        get() = TODO("Not yet implemented")
        set(value) {}

}