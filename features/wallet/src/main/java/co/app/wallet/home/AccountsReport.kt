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

package co.app.wallet.home

import androidx.appcompat.widget.Toolbar
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.app.common.account.UserAccount
import co.app.common.errors.NotFoundError
import co.app.report.Report
import co.app.report.ReportMeta
import co.app.views.ReportView
import co.app.wallet.domain.accounts.AccountsRepository
import co.app.wallet.domain.accounts.WalletAccount
import com.app.wallet.R
import promise.commons.Promise
import promise.commons.data.log.LogUtil
import promise.commons.model.Result
import promise.ui.PromiseAdapter
import promise.ui.model.Viewable
import kotlin.reflect.KClass
import promise.commons.model.List as PromiseList

@ReportMeta(
    headerRes = R.string.list_of_accounts,
    menu = R.menu.accounts_menu
)
class AccountsReport(
    private val userAccount: UserAccount,
    private val promise: Promise,
    private val accountsRepository: AccountsRepository
) : Report {

    private lateinit var walletAdapter: PromiseAdapter<WalletAccountHolder>

    override fun bind(reportView: ReportView) {
        reportView.onMenuItemClickListener = Toolbar.OnMenuItemClickListener {
            if (it.itemId == R.id.action_settings) {
                LogUtil.e(TAG, "settings clicked")
            }
            true
        }

        val accountsListRecyclerView =
            reportView.findViewById<RecyclerView>(R.id.accounts_recycler_view)
        accountsListRecyclerView.layoutManager = LinearLayoutManager(
            reportView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        walletAdapter = PromiseAdapter(
            object : PromiseAdapter.Listener<WalletAccountHolder> {
                override fun onClick(t: WalletAccountHolder, id: Int) {

                }
            }, true
        )
        accountsListRecyclerView.adapter = walletAdapter
        accountsRepository.getAllAccounts().observeForever {
            promise.executeOnUi {
                LogUtil.e(TAG, "accounts loaded: ", it)
                walletAdapter.args = null
                walletAdapter.add(
                    PromiseList(
                        it.map { WalletAccountHolder(it) }
                    )
                )
            }
        }
    }

    override fun layout(): Int = R.layout.account_list_report

    companion object {
        val TAG: String = LogUtil.makeTag(AccountsReport::class.java)
    }

}