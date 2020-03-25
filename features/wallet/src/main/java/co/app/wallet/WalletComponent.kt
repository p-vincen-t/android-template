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

import co.base.account.AccountComponent
import co.app.dashboard.DashboardScope
import co.app.wallet.domain.accounts.AccountsRepository
import dagger.BindsInstance
import dagger.Component

@DashboardScope
@Component(dependencies = [AccountComponent::class])
interface WalletComponent {

    fun inject(walletFragment: WalletFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance accountsRepository: AccountsRepository,
                   accountsComponent: AccountComponent): WalletComponent
    }
}