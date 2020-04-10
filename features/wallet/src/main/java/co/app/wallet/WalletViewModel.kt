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

package co.app.wallet

import androidx.appcompat.widget.Toolbar
import androidx.databinding.Bindable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import co.app.BaseViewModel
import co.app.common.account.UserAccount
import co.app.report.FooterParams
import co.app.report.Linear
import co.app.report.ListReport
import co.app.wallet.domain.accounts.AccountsRepository
import co.app.wallet.home.WalletAccountHolder
import com.app.wallet.R
import promise.commons.AndroidPromise
import promise.commons.data.log.LogUtil
import promise.ui.adapter.DataSource
import promise.commons.model.List as PromiseList

class WalletViewModel(
    private val userAccount: UserAccount?,
    private val promise: AndroidPromise,
    private val accountsRepository: AccountsRepository
) :
    BaseViewModel() {

    @Bindable
    var accountsReport: ListReport<WalletAccountHolder>? = null

    fun initData(lifecycleOwner: LifecycleOwner) {
        accountsReport = ListReport(
            title = "Wallet Accounts",
            menuClickListener = Toolbar.OnMenuItemClickListener {
                if (it.itemId == R.id.action_settings) {
                    LogUtil.e(TAG, "settings clicked")
                }
                true
            },
            menuRs = R.menu.accounts_menu,
            layoutType = Linear(orientation = RecyclerView.HORIZONTAL),
            dataSource = DataSource<WalletAccountHolder> { response,
                                                           _, _ ->
                accountsRepository.getAllAccounts().observe(lifecycleOwner, Observer {
                    LogUtil.e(TAG, "accounts loaded: ", it)
                    response.response(PromiseList(
                        it.map { WalletAccountHolder(it) }
                    ))
                })
            },
            footerParams = FooterParams(footerLayout = R.layout.accounts_report_footer)
        )
        //accountsReport = AccountsReport(lifecycleOwner, userAccount, promise, accountsRepository)
        notifyChanges()
    }

    companion object {
        val TAG = LogUtil.makeTag(WalletViewModel::class.java)
    }
}
