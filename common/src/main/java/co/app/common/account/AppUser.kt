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
import co.app.common.common.STUB
import co.app.common.photo.Photo
import com.google.auto.value.AutoValue

@AutoValue
abstract class AppUser {
    abstract val userId: ID
    abstract val userName: String?
    abstract val photo: Photo?

    companion object {
        val STUB: STUB<AppUser> = object : STUB<AppUser> {
            override fun stub(): AppUser = AutoValue_AppUser(ID.generate(), "username", null)
        }
    }

}