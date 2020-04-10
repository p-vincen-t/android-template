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

package co.app.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import co.app.R
import co.app.common.photo.Photo
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import promise.commons.data.log.LogUtil
import java.io.File

open class PhotoView : AppCompatImageView {

    var placeHolder: Drawable? = null
    var errorDrawable: Drawable? = null

    private var photo1: Photo? = null
    fun setPhoto(photo: Photo) {
        this.photo1 = photo
        invalidateView()
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.PhotoView, defStyle, 0
        )
        if (a.hasValue(R.styleable.PhotoView_placeHolderImageSrc)) {
            placeHolder = a.getDrawable(R.styleable.PhotoView_placeHolderImageSrc)
        }
        if (a.hasValue(R.styleable.PhotoView_errorImageSrc)) {
            errorDrawable = a.getDrawable(R.styleable.PhotoView_errorImageSrc)
        }
        a.recycle()
        scaleType = ScaleType.FIT_CENTER
        invalidateView()

    }

    private fun invalidateView() {
        super.invalidate()
        if (placeHolder != null) setImageDrawable(placeHolder)
        if (photo1 != null && photo1!!.url() != null) {
            Picasso.get().cancelRequest(this)
            if (photo1!!.isOffLine) {
                Picasso.get().load(File(photo1!!.url()))
                    .noPlaceholder()
                    .into(this, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                            if (errorDrawable != null) {
                                setImageDrawable(errorDrawable)
                                LogUtil.e(TAG, e)
                            }
                        }
                    })
            }
            else if (photo1!!.isOnLine) {
                Picasso.get().load(photo1!!.url())
                    .noPlaceholder()
                    .into(this, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                            if (errorDrawable != null) {
                                setImageDrawable(errorDrawable)
                                LogUtil.e(TAG, e)
                            }
                        }
                    })
            }

        }
    }

    companion object {
        val TAG: String = LogUtil.makeTag(PhotoView::class.java)
    }

}
