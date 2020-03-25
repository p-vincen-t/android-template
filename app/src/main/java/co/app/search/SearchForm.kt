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

import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import co.app.R
import co.app.report.Report
import co.app.report.ReportMeta
import co.app.report.ReportView
import co.app.common.search.Search
import com.google.android.material.floatingactionbutton.FloatingActionButton
import promise.commons.data.log.LogUtil

@ReportMeta
class SearchForm(
    private val lifecycleOwner: LifecycleOwner,
    private val searchFab: FloatingActionButton,
    private val listener: Listener
) : Report {

    private lateinit var parentView: View

    override fun bind(reportView: ReportView, view: View) {
        LogUtil.d(SearchActivity.TAG, "search form bind")
        parentView = view
        val searchView: SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {
                if (TextUtils.isEmpty(text)) {
                    if (searchFab.isShown) searchFab.hide()
                    return false
                }
                if (!searchFab.isShown) searchFab.show()
                listener.onSearch(Search().apply {
                    query = "$text submitted"
                })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    if (searchFab.isShown) searchFab.hide()
                    return false
                }
                if (!searchFab.isShown) searchFab.show()
                listener.onSearch(Search().apply {
                    query = newText!!
                })
                return true
            }
        })

        searchFab.setOnClickListener {

        }
    }

    override fun layout(): Int = R.layout.search_form

    interface Listener {
        fun onSearch(search: Search)
    }

}