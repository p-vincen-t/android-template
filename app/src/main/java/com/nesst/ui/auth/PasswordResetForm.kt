package com.nesst.ui.auth

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nesst.appdomain.errors.AuthError
import com.nesst.appdomain.session.Session
import com.nesst.ui.UIResult
import com.nesst.validators.isIdentifierValid
import promise.commons.Promise
import promise.commons.model.Result as PromiseResult

class PasswordResetForm(private val session: Session, private val promise: Promise) :
    BaseObservable(), AuthForm {

    private val _result = MutableLiveData<UIResult<*>>()

    val UIResult: LiveData<UIResult<*>> = _result

    override fun executeNext(viewModel: AuthViewModel) {

        dataValid = false
        progressLoading = true
        notifyChange()
        viewModel.signInForgotPasswordButtonEnabled = false
        viewModel.notifyChanges()
        promise.execute {
            session.resetPassword(identifier, PromiseResult<Boolean, AuthError>()
                .withCallBack {
                    promise.executeOnUi {
                        _result.value = UIResult.Success<Boolean>(it)
                    }
                }
                .withErrorCallBack {
                    promise.executeOnUi {
                        _result.value = UIResult.Error<AuthError>(it)
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

    @Bindable
    var identifierError: String = ""


    @Bindable
    var dataValid: Boolean = true

    @Bindable
    var progressLoading: Boolean = false

    override fun validate(args: Any?): Boolean {
        val valid = isIdentifierValid(identifier)
        if (!valid.first) {
            identifierError = valid.second!!
            notifyChange()
            return false
        }
        return true
    }
}