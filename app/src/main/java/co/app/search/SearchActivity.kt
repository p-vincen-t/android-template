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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import co.app.BaseActivity
import co.app.R
import co.app.domain.search.Search
import kotlinx.android.synthetic.main.activity_search.*
import promise.commons.data.log.LogUtil

class SearchActivity : BaseActivity(), SearchForm.Listener {

    lateinit var searchForm: SearchForm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        addBackButton()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        LogUtil.e(TAG, "search form init")
        searchForm = SearchForm(this, search_fab, this)
        search_report.report = searchForm
    }

    override fun onSearch(search: Search) {
        toolbar_layout.title = search.query
        LogUtil.e(TAG, "search query", search.query)
    }

    companion object {
        val TAG = LogUtil.makeTag(SearchActivity::class.java)
    }

}
