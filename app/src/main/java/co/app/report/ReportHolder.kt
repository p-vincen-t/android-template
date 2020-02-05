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

import android.view.View
import co.app.R
import co.app.views.ReportView
import promise.ui.model.Viewable

class ReportHolder(private val report: Report): Viewable {

    lateinit var reportView: ReportView

    override fun layout(): Int = R.layout.report_layout

    override fun bind(view: View, args: Any?) {
        if (report.javaClass.isAnnotationPresent(ReportMeta::class.java)) {
            val annotation = report.javaClass.getAnnotation(ReportMeta::class.java)!!
            reportView.menu = annotation.menu
            reportView.header = annotation.header
        }
        report.bind(reportView, args)
    }

    override fun init(view: View) {
        reportView = view.findViewById(R.id.reportView)
    }
}