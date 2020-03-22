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

package co.app.common.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import co.app.common.R
import promise.commons.data.log.LogUtil

class ReportFragment : Fragment() {

    private var reportView: ReportView? = null
    private var report: Report? = null
    private var view1: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val reportArea: FrameLayout = view.findViewById(R.id.report_area)
        LogUtil.d(TAG, "search fragment bind")
        if (report != null) {
            reportArea.visibility = View.VISIBLE
            reportArea.removeAllViews()
            if (view1 != null) reportArea.addView(view)
            else inflater.inflate(
                report!!.layout(),
                reportArea, true
            )
            report!!.bind(reportView!!, reportArea)
        } else reportArea.visibility = View.GONE
        return view
    }

    fun setReport(view: View?, report: ReportView) {
        this.reportView = report
        this.report = report.report
        this.view1 = view
    }

    companion object {
        val TAG = LogUtil.makeTag(ReportFragment::class.java)
    }
}
