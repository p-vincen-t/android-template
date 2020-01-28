/*
 * Copyright 2017, Nesst
 * Licensed under the Apache License, Version 2.0, "Nesst Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesst

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.nesst.appdomain.models.Photo
import com.nesst.views.PhotoView

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("errorText")
    fun showError(editText: TextInputLayout, err: String) {
        editText.error = err
    }

    @JvmStatic
    @BindingAdapter("visibility")
    fun changeVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("photo")
    fun photo(view: PhotoView, photo: Photo) {
        view.photo = photo
    }


}
