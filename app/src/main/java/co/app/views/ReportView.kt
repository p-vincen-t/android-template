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
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import co.app.R
import co.app.report.Report
import promise.commons.createInstance
import promise.ui.model.Viewable
import java.lang.IllegalStateException

class ReportView : LinearLayoutCompat {

    private var _header: String? = null
    private var _menu: Int = 0
    private var _reportClass: String? = null
    private var _report: Report? = null
    private var _onMenuItemClicked: Toolbar.OnMenuItemClickListener? = null

    var onMenuItemClickListner: Toolbar.OnMenuItemClickListener?
    get() = _onMenuItemClicked
    set(value) {
        _onMenuItemClicked = value
        invalidate()
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

    var report: Report
        get() = _report ?: throw IllegalStateException("Cant retrieve report not set")
        set(value) {
            _report = value
            invalidate()
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
            R.styleable.ReportView_header)
        _menu = a.getResourceId(
            R.styleable.ReportView_menu,
            0
        )

        if (a.hasValue(R.styleable.ReportView_report)) {
            _reportClass = a.getString(R.styleable.ReportView_report)
            _report = createInstance(Class.forName(_reportClass!!).kotlin)
        }

        a.recycle()
        invalidate()
    }

    override fun invalidate() {
        removeAllViews()
        if (header.isNotEmpty()) {
            val toolbar = Toolbar(context)
            toolbar.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            toolbar.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            addView(toolbar)
            toolbar.title = header
            if (_menu != 0) {
                toolbar.inflateMenu(_menu)
                if (_onMenuItemClicked != null) {
                    toolbar.setOnMenuItemClickListener(_onMenuItemClicked!!)
                }
            }
        }

        if (_report != null) {
            if (_report is Viewable) {

            }
        }
    }
}
