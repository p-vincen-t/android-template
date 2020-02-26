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

package co.app.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import co.app.R
import co.app.report.Report
import co.app.report.ReportMeta
import com.google.android.material.appbar.MaterialToolbar
import promise.commons.createInstance

class ReportView : LinearLayoutCompat, LifecycleOwner {

    private val registry: LifecycleRegistry = LifecycleRegistry(this)

    private var _header: String? = null
    private var _menu: Int = 0
    private var _reportClass: String? = null
    private var _report: Report? = null
    private var _onMenuItemClicked: Toolbar.OnMenuItemClickListener? = null

    private var toolbar: MaterialToolbar? = null
    private var frameLayout: FrameLayout? = null

    var view: View? = null

    var onMenuItemClickListener: Toolbar.OnMenuItemClickListener?
        get() = _onMenuItemClicked
        set(value) {
            _onMenuItemClicked = value
            if (_menu != 0 && toolbar != null) {
                toolbar!!.inflateMenu(_menu)
                if (_onMenuItemClicked != null) {
                    toolbar!!.setOnMenuItemClickListener(_onMenuItemClicked!!)
                }
            }
        }

    /**
     * The font color
     */
    var header: String
        get() = _header ?: ""
        set(value) {
            _header = value
            invalidate()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    var menu: Int
        get() = _menu
        set(value) {
            _menu = value
            invalidate()
        }

    var report: Report?
        get() = _report
        set(value) {
            _report = value
            if (_report != null) {
                if (_report!!.javaClass.isAnnotationPresent(ReportMeta::class.java)) {
                    val annotation = _report!!.javaClass.getAnnotation(ReportMeta::class.java)!!
                    menu = annotation.menu
                    header = if (annotation.headerRes != 0) {
                        context.getString(annotation.headerRes)
                    } else annotation.header
                }
                frameLayout!!.visibility = View.VISIBLE
                frameLayout!!.removeAllViews()
                if (view != null) frameLayout!!.addView(view)
                else LayoutInflater.from(context).inflate(
                    _report!!.layout(),
                    frameLayout, true
                )
                report!!.bind(this)
            } else frameLayout!!.visibility = View.GONE
            if (_report == null) return
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
        orientation = VERTICAL
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ReportView, defStyle, 0
        )

        _header = a.getString(
            R.styleable.ReportView_header
        )
        _menu = a.getResourceId(
            R.styleable.ReportView_menu,
            0
        )

        if (a.hasValue(R.styleable.ReportView_report)) {
            _reportClass = a.getString(R.styleable.ReportView_report)
            _report = createInstance(Class.forName(_reportClass!!).kotlin)
        }
        a.recycle()
        toolbar = MaterialToolbar(context)
        val toolBarParams = ViewGroup.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        toolbar!!.layoutParams = toolBarParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            toolbar!!.setBackgroundColor(
                resources.getColor(
                    R.color.color_surface,
                    context.theme
                )
            )
        else toolbar!!.setBackgroundColor(resources.getColor(R.color.color_surface))
        addView(toolbar)

        toolbar!!.title = header
        if (header.isEmpty() && _menu == 0) toolbar!!.visibility = View.GONE
        else toolbar!!.visibility = View.VISIBLE
        frameLayout = FrameLayout(context)
        val frameLayoutParams = ViewGroup.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        frameLayout!!.layoutParams = frameLayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            frameLayout!!.setBackgroundColor(
                resources.getColor(
                    R.color.color_surface,
                    context.theme
                )
            )
        else frameLayout!!.setBackgroundColor(resources.getColor(R.color.color_surface))
        addView(frameLayout)
        if (_report != null) {
            frameLayout!!.visibility = View.VISIBLE
            if (view != null) frameLayout!!.addView(view)
            else LayoutInflater.from(context).inflate(
                _report!!.layout(),
                frameLayout, true
            )
            frameLayout!!.removeAllViews()
            report!!.bind(this)
        } else frameLayout!!.visibility = View.GONE
    }


    override fun getLifecycle(): Lifecycle = registry
}
