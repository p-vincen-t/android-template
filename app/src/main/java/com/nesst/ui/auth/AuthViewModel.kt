package com.nesst.ui.auth

import android.view.View
import androidx.databinding.Bindable
import com.nesst.appdomain.session.Session
import com.nesst.ui.BaseViewModel
import promise.commons.Promise
import promise.commons.data.log.LogUtil

class AuthViewModel(private val session: Session, private val promise: Promise) : BaseViewModel() {

    val TAG = LogUtil.makeTag(AuthViewModel::class.java)

    @Bindable
    var nextAction = ACTION_LOGIN

    val loginForm: LoginForm by lazy { LoginForm(session, promise) }

    val registrationForm: RegistrationForm by lazy { RegistrationForm(session, promise) }

    val passwordResetForm: PasswordResetForm by lazy { PasswordResetForm(session, promise) }

    @Bindable
    var authActionTitle: String = "Sign In to Continue"

    @Bindable
    var authActionButtonText = "Sign In"

    @Bindable
    var signInResetText = "Forgot password?"

    @Bindable
    var showWelcomeMessage = true

    @Bindable
    var identifierInputVisible = true

    @Bindable
    var signInForgotPasswordButtonEnabled = true

    fun authButtonClicked(_v: View) {
        when (nextAction) {
            ACTION_LOGIN -> {
                if (loginForm.validate(null)) loginForm.executeNext(this)
                else notifyChanges()
            }
            ACTION_RESET -> {
                if (passwordResetForm.validate(null)) passwordResetForm.executeNext(this)
                else notifyChanges()
            }
            ACTION_REGISTER -> {
                if (registrationForm.validate(null)) registrationForm.executeNext(this)
                else notifyChanges()
            }
        }
    }

    fun loginOrForgotPasswordClicked(v: View) {
        if (nextAction == ACTION_LOGIN) {
            identifierInputVisible = false
            loginForm.clear()
            authActionTitle = "Reset password to continue"
            authActionButtonText = "Reset Password"
            signInResetText = "Login?"
            nextAction = ACTION_RESET
            notifyChanges()

        } else if (nextAction == ACTION_RESET) {
            identifierInputVisible = true
            authActionTitle = "Sign to continue"
            authActionButtonText = "Sign in"
            signInResetText = "Forgot Password?"
            loginForm.clear()
            nextAction = ACTION_LOGIN
            notifyChanges()

        }
    }

    fun createAccountClicked(v: View) {
        nextAction = ACTION_REGISTER
        notifyChanges()
    }

    companion object {
        @JvmField
        val ACTION_LOGIN = 1
        @JvmField
        val ACTION_RESET = 2
        @JvmField
        val ACTION_REGISTER = 3
    }
}
