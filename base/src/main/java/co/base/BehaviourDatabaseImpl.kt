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

package co.base

import co.app.common.ID
import co.app.common.photo.Photo
import co.app.common.photo.PhotoDatabase
import co.app.common.search.Search
import co.app.common.search.SearchDatabase
import co.base.common.PhotoRecord
import co.base.common.PhotoRecordTable
import co.base.search.SearchRecord
import co.base.search.SearchRecordTable
import promise.db.Database
import promise.db.FastDatabase
import promise.model.IdentifiableList
import javax.inject.Inject

@Database(
    tables = [
        SearchRecordTable::class,
        PhotoRecordTable::class
    ]
)
@DataScope
class BehaviourDatabaseImpl @Inject constructor() : SearchDatabase, PhotoDatabase {

    override fun save(search: Search) {
        searchRecordTable.save(SearchRecord.from(search))
    }

    override fun save(searched: List<Search>) {
        searchRecordTable.save(IdentifiableList(searched.map {
            SearchRecord.from(it)
        }))
    }

    override fun getPhotoByRef(refName: String, id: ID): Photo? =
        photoRecordTable.getPhotoRecordByRef(refName, id)?.toPhoto()

    override fun savePhoto(photo: Photo, refName: String, id: ID): Photo {
        val photoRecord = PhotoRecord()
        photoRecord.refName = refName
        photoRecord.refId = id
        photoRecord.url = photo.url()
        photoRecord.type = photo.type()
        photoRecordTable.save(photoRecord)
        return photo
    }

    override fun savePhotos(photos: List<Photo>, ids: Array<ID>, refName: String): Boolean {
        if (photos.size != ids.size) throw IllegalArgumentException("photos length must be same as ids length")
        val photoRecords = IdentifiableList<PhotoRecord>()
        photos.forEachIndexed { index, photo ->
            val photoRecord = PhotoRecord()
            photoRecord.refName = refName
            photoRecord.refId = ids[index]
            photoRecord.url = photo.url()
            photoRecord.type = photo.type()
            photoRecords.add(photoRecord)
        }
        return photoRecordTable.save(photoRecords)
    }

    override fun getPhotosByRef(refName: String, ids: Array<ID>): List<Photo> =
        photoRecordTable.getPhotoRecordsByRef(refName, ids).map { it.toPhoto() }

    companion object {
        val instance: FastDatabase =
            FastDatabase.createDatabase(BehaviourDatabaseImpl::class.java, "behaviour_db")
        val searchRecordTable: SearchRecordTable by lazy {
            instance.obtain<SearchRecordTable>(SearchRecordTable::class.java)
        }

        val photoRecordTable: PhotoRecordTable by lazy {
            instance.obtain<PhotoRecordTable>(PhotoRecordTable::class.java)
        }
    }
}