package com.nesst.ui.auth

import com.nesst.ui.ViewForm

interface AuthForm : ViewForm {
    fun executeNext(viewModel: AuthViewModel)
    fun clear()
}