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

package co.app.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.app.common.account.AccountManager
import co.app.common.account.UserAccount
import promise.commons.AndroidPromise
import promise.commons.model.List

class DashboardViewModel(
    val userAccount: AccountManager,
    private val promise: AndroidPromise
) : ViewModel() {

    private val _accountsResult = MutableLiveData<List<UserAccount.UserChildAccount>>()

    val accountsResult: LiveData<List<UserAccount.UserChildAccount>> = _accountsResult

    fun fetchAccounts() {
        if (userAccount.provideAccount() == null) return
        _accountsResult.value = List(userAccount.provideAccount()!!.childAccounts!!)
    }

}
