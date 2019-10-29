package com.nesst.ui.auth

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nesst.ui.Result
import com.nesst.validators.isEmailValid
import com.nesst.validators.isPasswordValid
import com.nesstbase.auth.Session
import com.nesstbase.errors.AuthError
import promise.commons.Promise
import promise.commons.model.Result as PromiseResult

class RegistrationForm(private val session: Session, private val promise: Promise) :
    BaseObservable(), AuthForm {

    private val _result = MutableLiveData<Result<*>>()

    val result: LiveData<Result<*>> = _result

    override fun executeNext(viewModel: AuthViewModel) {

        dataValid = false
        progressLoading = true
        notifyChange()
        viewModel.signInForgotPasswordButtonEnabled = false
        viewModel.notifyChanges()
        promise.execute {
            session.register(names,
                email, phoneNumber,
                password,
                PromiseResult<Boolean, AuthError>()
                    .withCallBack {
                        promise.executeOnUi {
                            _result.value = Result.Success<Boolean>(it)
                        }
                    }
                    .withErrorCallBack {
                        promise.executeOnUi {
                            _result.value = Result.Error<AuthError>(it)
                            dataValid = true
                            notifyChange()
                            viewModel.signInForgotPasswordButtonEnabled = false
                            viewModel.notifyChanges()
                        }
                    })
        }
    }

    override fun clear() {
    }

    var names: String = ""

    var email: String = ""

    var phoneNumber: String = ""

    var password: String = ""

    var confirmPassword: String = ""

    @Bindable
    var namesError: String = ""

    @Bindable
    var emailError: String = ""

    @Bindable
    var phoneNumberError: String = ""

    @Bindable
    var passwordError: String = ""

    @Bindable
    var confirmPasswordError: String = ""

    @Bindable
    var dataValid: Boolean = true

    @Bindable
    var progressLoading: Boolean = false

    override fun validate(args: Any?): Boolean {
        val valid = isEmailValid(email)
        if (!valid) {
            emailError = "Kindly check your email"
            notifyChange()
            return false
        }
        if (!isPasswordValid(password)) {
            passwordError = "Kindly check your password"
            notifyChange()
            return false
        }
        return true
    }
}