package com.nesst.ui.dashboard

import androidx.lifecycle.ViewModel
import com.nesstbase.auth.Session
import com.nesstbase.auth.User

class DashboardViewModel(private val session: Session) : ViewModel() {

    var user: User = session.user()

}
