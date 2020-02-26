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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import co.app.BaseFragment
import com.app.wallet.R
import com.app.wallet.databinding.WalletFragmentBinding
import javax.inject.Inject

class WalletFragment : BaseFragment() {

    @Inject
    lateinit var walletViewModelFactory: WalletViewModelFactory

    private lateinit var viewModel: WalletViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<WalletFragmentBinding>(
            inflater,
            R.layout.wallet_fragment,
            container,
            false
        )
        val moduleRegistrar = ModuleRegistrar.instance()

        DaggerWalletComponent.factory()
            .create(
                moduleRegistrar.dataComponent.accountsRepository(),
                moduleRegistrar.accountsComponent
            )
            .inject(this)
        viewModel = ViewModelProvider(this, walletViewModelFactory).get(WalletViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initData()
    }

}
