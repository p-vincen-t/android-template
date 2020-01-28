package com.nesst.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nesst.appdomain.session.Session
import promise.commons.Promise
import javax.inject.Inject

/**
 * ViewModel provider factory to instantiate RegisterViewModel.
 * Required given RegisterViewModel has a non-empty constructor
 */
@AuthScope
class AuthViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    @Inject
    lateinit var session: Session
    @Inject
    lateinit var promise: Promise

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                session,
                promise
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
