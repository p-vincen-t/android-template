package com.nesst.ui.auth

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nesst.R
import com.nesst.databinding.ActivityAuthBinding
import com.nesst.ui.BaseActivity
import com.nesst.ui.DaggerUiComponent
import com.nesst.ui.DialogButton
import com.nesst.ui.dialog
import com.nesst.ui.Result
import javax.inject.Inject


class AuthActivity : BaseActivity() {

    private lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var authViewModelFactory: AuthViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        addBackButton()

        val binding = DataBindingUtil.setContentView<ActivityAuthBinding>(
            this,
            R.layout.activity_auth
        )
            .apply {
                this.lifecycleOwner = this@AuthActivity
            }

        DaggerUiComponent.builder()
            .appComponent(app.appComponent)
            .build().inject(this)

        authViewModel = ViewModelProvider(this, authViewModelFactory)
            .get(AuthViewModel::class.java)

        binding.viewModel = authViewModel
        binding.loginForm = authViewModel.loginForm
        binding.registrationForm = authViewModel.registrationForm
        binding.passwordResetForm = authViewModel.passwordResetForm
        binding.nextAction = authViewModel.nextAction
        binding.lifecycleOwner = this
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        authViewModel.loginForm.result.observe(this@AuthActivity, Observer {
            when (it ?: return@Observer) {
                is Result.Success<*> -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else -> dialog("Authentication Failed",
                    it.toString(),
                    DialogButton("Ok") { finish() }
                )
            }
        })
    }
}
