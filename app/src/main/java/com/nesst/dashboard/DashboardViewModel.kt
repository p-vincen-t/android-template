package com.nesst.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nesst.appdomain.session.Account
import com.nesst.appdomain.session.Session
import promise.commons.Promise
import promise.commons.model.List

class DashboardViewModel(private val session: Session, private val promise: Promise) : ViewModel() {

    private val _accountsResult = MutableLiveData<List<Account>>()

    val accountsResult: LiveData<List<Account>> = _accountsResult

    fun fetchAccounts() {
        _accountsResult.value = List.generate(3) { Account("Peter", "v@gmail.com") }
    }

}
