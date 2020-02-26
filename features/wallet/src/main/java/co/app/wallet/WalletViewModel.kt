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

import androidx.databinding.Bindable
import co.app.BaseViewModel
import co.app.common.account.UserAccount
import co.app.wallet.domain.accounts.AccountsRepository
import co.app.wallet.home.AccountsReport
import promise.commons.Promise

class WalletViewModel(private val userAccount: UserAccount,
                      private val promise: Promise,
                      private val accountsRepository: AccountsRepository) :
    BaseViewModel() {

    @Bindable
    var accountsReport: AccountsReport? = null

    fun initData() {
        accountsReport = AccountsReport(userAccount, promise, accountsRepository)
        notifyChanges()
    }
}
