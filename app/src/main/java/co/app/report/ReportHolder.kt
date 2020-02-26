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
import co.app.views.ReportView
import promise.ui.model.Viewable

class ReportHolder(private val report: Report) : Viewable {

    lateinit var reportView: ReportView

    override fun layout(): Int = report.layout()

    override fun bind(view: View, args: Any?) {
        reportView.report = report
    }

    override fun init(view: View) {
        reportView = ReportView(view.context)
        reportView.view = view
    }
}