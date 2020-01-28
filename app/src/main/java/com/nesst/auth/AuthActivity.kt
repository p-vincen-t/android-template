package com.nesst.auth

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nesst.*
import com.nesst.databinding.ActivityAuthBinding
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

        DaggerAuthComponent.builder()
            .sessionComponent(app.sessionComponent)
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
        authViewModel.loginForm.uIResult.observe(this@AuthActivity, Observer {
            when (it ?: return@Observer) {
                is UIResult.Success<*> -> {
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
