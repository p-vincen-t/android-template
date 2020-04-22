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
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.report_list_item.*
import promise.ui.Viewable

class ReportHolder(private val report: Report) : Viewable, LayoutContainer {

    lateinit var viewable: View

    override fun layout(): Int = R.layout.report_list_item

    override fun bind(view: View, args: Any?) {
        parentView.report = report
    }

    override fun init(view: View) {
        this.viewable = view
    }

    override val containerView: View?
        get() = viewable

    override fun toString(): String = "ReportHolder(report=$report)"


}