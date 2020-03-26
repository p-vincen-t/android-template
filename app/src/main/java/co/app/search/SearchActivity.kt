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

package co.app.search

import android.os.Bundle
import co.app.BaseActivity
import co.app.R
import co.app.common.search.Search
import co.app.common.search.SearchRepository
import co.app.dashboard.DaggerDashboardComponent
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import javax.inject.Inject

class SearchActivity : BaseActivity(), SearchForm.Listener {

    lateinit var searchForm: SearchForm

    @Inject
    lateinit var searchRepository: SearchRepository

    @Inject
    lateinit var androidPromise: AndroidPromise

    lateinit var searchReport: SearchReport

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        addBackButton()

        DaggerDashboardComponent.factory().create(
                app.reposComponent().searchRepository(), app.accountComponent
            )
            .inject(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        LogUtil.e(TAG, "search form init")
        searchForm = SearchForm(this, androidPromise, app.compositeDisposable, search_fab, this)
        search_report.report = searchForm

        searchReport = SearchReport(this, app, searchRepository, androidPromise)
        search_report_view.report = searchReport
    }

    override fun onSearch(search: Search) {
        toolbar_layout.title = search.query
        searchReport.search(this, search)
    }

    companion object {
        val TAG: String = LogUtil.makeTag(SearchActivity::class.java)
    }

}
