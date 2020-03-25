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

package co.app.attachment

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.LongDef
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AttachmentModel(
    @ItemType val type: Long,
    val itemLabel: String = "",
    val itemIcon: Int = 0,
    val hasBackground: Boolean = true,
    @ShapeType val backgroundType: Long = TYPE_CIRCLE,
    val itemBackgroundColor: Int = 0
) : Parcelable {

    companion object {

        fun camera(): AttachmentModel = AttachmentModel(
            ITEM_CAMERA
        )

        fun gallery(): AttachmentModel = AttachmentModel(
            ITEM_GALLERY
        )

        fun files(): AttachmentModel = AttachmentModel(
            ITEM_FILES
        )

        fun video(): AttachmentModel = AttachmentModel(
            ITEM_VIDEO
        )

        @LongDef(
            TYPE_CIRCLE,
            TYPE_SQUARE,
            TYPE_ROUNDED_SQUARE
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class ShapeType

        const val TYPE_CIRCLE = 0L
        const val TYPE_SQUARE = 1L
        const val TYPE_ROUNDED_SQUARE = 2L

        @LongDef(
            ITEM_CAMERA,
            ITEM_GALLERY,
            ITEM_VIDEO,
            ITEM_FILES
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class ItemType

        const val ITEM_CAMERA = 10L
        const val ITEM_GALLERY = 11L
        const val ITEM_VIDEO = 12L
        const val ITEM_FILES = 14L
    }
}