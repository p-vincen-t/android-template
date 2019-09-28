package com.nesst.ui

import com.nesst.ui.auth.AuthActivity
import com.nesst.ui.dashboard.DashboardActivity
import com.nesstbase.AppComponent
import com.nesstbase.scopes.UiScope
import dagger.Component

@Component(dependencies = [AppComponent::class])
@UiScope
interface UiComponent {

    fun inject(authActivity: AuthActivity)
    fun inject(dashboardActivity: DashboardActivity)
    fun inject(baseActivity: BaseActivity)
}