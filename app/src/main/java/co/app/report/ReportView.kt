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

package co.app.report

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import co.app.R
import com.google.android.material.appbar.MaterialToolbar
import promise.ui.adapter.ViewHolder

class ReportView : LinearLayoutCompat, ViewHolder {

    private var _header: String? = null
    private var _menu: Int = 0
    private var _report: co.app.report.Report? = null
    private var _onMenuItemClicked: Toolbar.OnMenuItemClickListener? = null
    private lateinit var toolbar: MaterialToolbar
    private lateinit var fragmentContainer: FrameLayout

    fun setArgs(
        report: co.app.report.Report,
        header: String,
        menu: Int = 0,
        menuListener: Toolbar.OnMenuItemClickListener? = null
    ) {
        _report = report
        _header = header
        _menu = menu
        _onMenuItemClicked = menuListener
    }

    var onMenuItemClickListener: Toolbar.OnMenuItemClickListener?
        get() = _onMenuItemClicked
        set(value) {
            _onMenuItemClicked = value
            setMenuListener()
        }

    private fun setMenuListener() {
        if (_onMenuItemClicked != null) toolbar.setOnMenuItemClickListener(_onMenuItemClicked!!)
    }

    /**
     * The font color
     */
    private var header: String
        get() = _header ?: ""
        set(value) {
            _header = value
            setHeader()
        }

    private fun setHeader() {
        if (!TextUtils.isEmpty(_header)) {
            toolbar.visibility = View.VISIBLE
            toolbar.title = _header
        } else toolbar.visibility = View.GONE
    }

    /**
     * In the example view, this dimension is the font size.
     */
    private var menu: Int
        get() = _menu
        set(value) {
            _menu = value
            inflateMenu()
        }

    private fun inflateMenu() {
        if (_menu != 0) toolbar.inflateMenu(_menu)
    }

    var report: co.app.report.Report?
        get() = _report
        set(value) {
            _report = value
            setReport()
        }

    private fun setReport() {
        if (_report != null) {
            if (_report!!.javaClass.isAnnotationPresent(co.app.report.ReportMeta::class.java)) {
                val annotation = _report!!.javaClass.getAnnotation(co.app.report.ReportMeta::class.java)!!
                menu = annotation.menu
                header = if (annotation.headerRes != 0) {
                    context.getString(annotation.headerRes)
                } else annotation.header
                fragmentContainer.visibility = View.VISIBLE
                fragmentContainer.removeAllViews()
                LayoutInflater.from(context).inflate(
                    report!!.layout(),
                    fragmentContainer, true
                )
                report!!.bind(this, fragmentContainer)
            } else throw IllegalStateException("Reports must be annotated with @ReportMeta")
        } else fragmentContainer.visibility = View.GONE
    }

    constructor(context: Context) : super(context)

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

        a.recycle()
        val view = LayoutInflater.from(context).inflate(R.layout.report, this, true)
        init(view)
        bind(view, null)
    }

    override fun bind(view: View?, args: Any?) {
        setHeader()
        inflateMenu()
        setMenuListener()
        setReport()
    }

    override fun init(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        fragmentContainer = view.findViewById(R.id.container)
    }
}
