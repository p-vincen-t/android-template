package com.nesst.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nesstbase.auth.Session
import com.nesstbase.scopes.UiScope
import javax.inject.Inject

/**
 * ViewModel provider factory to instantiate RegisterViewModel.
 * Required given RegisterViewModel has a non-empty constructor
 */
@UiScope
class DashboardViewModelFactory @Inject constructor(private val session: Session) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                session = session
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
