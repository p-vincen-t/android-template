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

package co.app.auth.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import co.app.BaseFragment
import co.app.FeatureFragment
import co.app.auth.DaggerAuthComponent
import co.app.auth.ModuleRegistrar
import co.app.auth.R
import co.app.auth.SessionComponent
import co.app.auth.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : FeatureFragment() {

    @Inject
    lateinit var loginViewModelFactory: LoginViewModelFactory

    private val sessionComponent: SessionComponent by lazy {
        ModuleRegistrar.instance().sessionComponent
    }

    lateinit var loginViewModel: LoginViewModel

    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        DaggerAuthComponent.builder()
            .sessionComponent(sessionComponent)
            .build()
            .inject(this)

        loginViewModel = ViewModelProvider(this, loginViewModelFactory)[LoginViewModel::class.java]
        binding.viewModel = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        loginViewModel.uIResult.observe(viewLifecycleOwner, Observer {

            activity?.finish()
        })

        forgotPasswordButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }

        createAccountButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

}
