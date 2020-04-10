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

package co.app.common.photo

import co.app.common.ID

/**
 *
 */
interface PhotoDatabase {
    /**
     * gets a photo matching the [refName] and [id] e.g a user will have
     * a photo match with ref name as user_photo and id as the id of the user
     */
    fun getPhotoByRef(refName: String, id: ID): Photo?

    /**
     * saves a photo with the [refName] and [id] of the associated entity
     */
    fun savePhoto(photo: Photo, refName: String, id: ID): Photo

    /**
     * saves multiple [photos] and generated [ids] and [refName]
      */
    fun savePhotos(photos: List<Photo>, ids: Array<ID>, refName: String): Boolean

    /**
     * gets all the photos with [refName] and [ids]
     */
    fun getPhotosByRef(refName: String, ids: Array<ID>): List<Photo>
}