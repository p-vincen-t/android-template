package com.nesst.ui.viewHolders

import android.view.View
import com.nesst.R
import com.nesstbase.auth.Account
import promise.ui.model.Viewable

class NavigationAccountViewHolder(private val account: Account): Viewable {
    override fun layout(): Int = R.layout.account_nav_layout

    override fun bind(view: View?) {

    }

    override fun init(view: View?) {

    }
}