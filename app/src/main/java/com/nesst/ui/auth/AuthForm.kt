package com.nesst.ui.auth

import com.nesst.tools.ViewForm

interface AuthForm : ViewForm {
    fun executeNext(viewModel: AuthViewModel)
    fun clear()
}