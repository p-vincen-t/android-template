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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import co.app.BaseActivity
import co.app.BaseFragment
import co.app.dsl.prepareAdapter
import co.app.report.ReportHolder
import co.app.wallet.home.ExpenseStructureReport
import com.app.wallet.R
import com.app.wallet.databinding.WalletFragmentBinding
import kotlinx.android.synthetic.main.wallet_fragment.*
import promise.ui.adapter.PromiseAdapter
import javax.inject.Inject

@SuppressLint("Registered")
class WalletActivity : BaseActivity() {

    @Inject
    lateinit var walletViewModelFactory: WalletViewModelFactory

    private lateinit var promiseAdapter: PromiseAdapter<ReportHolder>

    private lateinit var viewModel: WalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<WalletFragmentBinding>(this, R.layout.wallet_fragment)
        val moduleRegistrar = ModuleRegistrar.instance()

        DaggerWalletComponent.factory()
            .create(
                moduleRegistrar.dataComponent.accountsRepository(),
                moduleRegistrar.accountsComponent
            )
            .inject(this)
        viewModel = ViewModelProvider(this, walletViewModelFactory).get(WalletViewModel::class.java)
        binding.viewModel = viewModel
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.initData(this)

        promiseAdapter = wallet_reports_recycler_view.prepareAdapter()

        promiseAdapter.add(
            ReportHolder(
                ExpenseStructureReport(
                    this
                )
            )
        )
    }

}
