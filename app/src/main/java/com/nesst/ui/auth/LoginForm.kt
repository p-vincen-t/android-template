package com.nesst.ui.auth

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nesst.tools.Result
import com.nesst.ui.BaseViewModel
import com.nesst.validators.isIdentifierValid
import com.nesst.validators.isPasswordValid
import com.nesstbase.auth.Session
import com.nesstbase.auth.User
import com.nesstbase.errors.AuthError
import promise.Promise
import promise.model.Result as PromiseResult

class LoginForm(private val session: Session, private val promise: Promise) : BaseObservable(), AuthForm {

    private val _result = MutableLiveData<Result<*>>()

    val result: LiveData<Result<*>> = _result

    override fun executeNext(viewModel: AuthViewModel) {

        dataValid = false
        progressLoading = true
        notifyChange()
        viewModel.signInForgotPasswordButtonEnabled = false
        viewModel.notifyChanges()
        promise.execute {
            session.login(identifier, password, PromiseResult<User, AuthError>()
                .responseCallBack {
                    promise.executeOnUi {
                        _result.value = Result.Success<User>(it)
                    }
                }
                .errorCallBack {
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

    var identifier: String = ""

    var password: String = ""

    @Bindable
    var identifierError: String = ""

    @Bindable
    var passwordError: String = ""

    @Bindable
    var dataValid: Boolean = true

    @Bindable
    var progressLoading: Boolean = false

    override fun validate(args: Any?): Boolean {
        if (args != null) {
            if (args is BaseViewModel) {
                val valid = isIdentifierValid(identifier)
                if (!valid.first) {
                    identifierError = valid.second!!
                    args.notifyChanges()
                    return false
                }
                if (!isPasswordValid(password)) {
                    passwordError = "Check your password"
                    args.notifyChanges()
                    return false
                }
                return true
            }
        }
        return false
    }
}