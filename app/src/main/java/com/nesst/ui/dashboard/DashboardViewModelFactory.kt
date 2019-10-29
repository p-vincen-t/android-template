package com.nesst.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nesstbase.auth.Session
import com.nesstbase.scopes.UiScope
import promise.commons.Promise
import javax.inject.Inject

/**
 * ViewModel provider factory to instantiate RegisterViewModel.
 * Required given RegisterViewModel has a non-empty constructor
 */
@UiScope
class DashboardViewModelFactory @Inject constructor() : ViewModelProvider.Factory {

    @Inject
    lateinit var session: Session
    @Inject
    lateinit var promise: Promise

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                session = session,
                promise = promise
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
