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
import co.app.BaseFragment
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.dashboard.DaggerDashboardComponent
import co.app.search.SearchReport
import kotlinx.android.synthetic.main.main_fragment.*
import promise.commons.AndroidPromise
import javax.inject.Inject

class MainFragment : BaseFragment() {

    lateinit var searchReport: SearchReport

    @Inject
    lateinit var searchRepository: SearchRepository

    @Inject
    lateinit var androidPromise: AndroidPromise

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerDashboardComponent.factory().create(
                app.reposComponent().searchRepository(), app.accountComponent
            )
            .inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchReport = SearchReport(
            viewLifecycleOwner,
            app,
            searchRepository,
            androidPromise
        )
        search_report.report = searchReport
        searchReport.search(requireContext(), Search())
    }
}
