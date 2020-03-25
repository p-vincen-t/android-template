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

package co.app.dashboard.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import co.app.BaseFragment
import co.app.R
import co.app.dsl.prepareAdapter
import co.app.report.ReportHolder
import co.app.dashboard.recents.RecentReport
import kotlinx.android.synthetic.main.main_fragment.*
import promise.ui.adapter.PromiseAdapter

class MainFragment : BaseFragment() {
    private lateinit var dataAdapter: PromiseAdapter<ReportHolder>

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        dataAdapter = reports_recycler_view.prepareAdapter {
            args = null
        }

        //reports_loader.showLoading(AppLoaderProgress("Loading, please wait...") , null)

        reports_loader.showContent()
        dataAdapter.add(
            ReportHolder(
                RecentReport(
                    viewLifecycleOwner
                )
            )
        )

    }

}
