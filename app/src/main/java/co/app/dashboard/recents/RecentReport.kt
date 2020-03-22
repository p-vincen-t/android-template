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

package co.app.dashboard.recents

import android.view.View
import androidx.lifecycle.LifecycleOwner
import co.app.R
import co.app.common.report.Report
import co.app.common.report.ReportMeta
import co.app.common.report.ReportView

@ReportMeta(
    header = "Recent"
)
class RecentReport(private val lifecycleOwner: LifecycleOwner) : Report {

    override fun bind(reportView: ReportView, view: View) {
    }

    override fun layout(): Int = R.layout.recents_report

}