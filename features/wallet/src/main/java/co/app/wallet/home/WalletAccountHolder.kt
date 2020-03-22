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

import android.view.View
import co.app.common.BindingAdapters
import co.app.wallet.domain.accounts.WalletAccount
import com.app.wallet.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.account_type_item_layout.*
import promise.ui.Viewable

class WalletAccountHolder (private val walletAccount: WalletAccount): Viewable, LayoutContainer {
    lateinit var view: View

    override fun layout(): Int = R.layout.account_type_item_layout

    override fun bind(view: View?, args: Any?) {
        shimmersFrameLayout.stopShimmer()
        shimmersFrameLayout.setShimmer(null)
        accountTitleTextView.text = walletAccount.name()
        BindingAdapters.bindMoney(accountBalanceTextView, walletAccount.amount())
    }

    override fun init(view: View) {
        this.view = view
    }

    override val containerView: View?
        get() = view
}