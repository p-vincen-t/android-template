package com.nesst.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.nesst.ui.views.PhotoView
import com.nesstbase.models.Photo

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
