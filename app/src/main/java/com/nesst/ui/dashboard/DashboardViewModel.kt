package com.nesst.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nesst.ui.Result
import com.nesstbase.auth.Session
import com.nesstbase.auth.Account
import promise.commons.Promise
import promise.commons.model.List

class DashboardViewModel(private val session: Session, private val promise: Promise) : ViewModel() {

    private val _accountsResult = MutableLiveData<List<Account>>()

    val accountsResult: LiveData<List<Account>> = _accountsResult

    fun fetchAccounts() {
        _accountsResult.value = List.generate(3) { Account("Peter", "v@gmail.com") }
    }

}
