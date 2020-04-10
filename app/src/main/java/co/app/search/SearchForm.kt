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

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LifecycleOwner
import co.app.R
import co.app.common.search.Search
import co.app.report.Report
import co.app.report.ReportMeta
import co.app.report.ReportView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import java.util.concurrent.TimeUnit

@ReportMeta
class SearchForm(
    private val lifecycleOwner: LifecycleOwner,
    private val androidPromise: AndroidPromise,
    private val compositeDisposable: CompositeDisposable,
    private val searchFab: FloatingActionButton,
    private val listener: Listener
) : Report {

    private lateinit var parentView: View

    override fun bind(reportView: ReportView, view: View) {
        LogUtil.d(SearchActivity.TAG, "search form bind")
        parentView = view
        val searchView: SearchView = view.findViewById(R.id.searchView)
        compositeDisposable.add(
            RxSearchObservable.fromView(searchView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.from(androidPromise.executor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listener.onSearch(Search().apply {
                        query = it
                    })
                }
        )

        searchFab.setOnClickListener {

        }
    }

    override fun layout(): Int = R.layout.search_form

    interface Listener {
        fun onSearch(search: Search)
    }

}

object RxSearchObservable {

    fun fromView(searchView: SearchView): Observable<String> {
        val subject = PublishSubject.create<String>()
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                subject.onComplete()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })
        return subject
    }
}