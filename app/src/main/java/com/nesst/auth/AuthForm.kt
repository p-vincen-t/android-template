package com.nesst.auth

import com.nesst.ViewForm

interface AuthForm : ViewForm {
    fun executeNext(viewModel: AuthViewModel)
    fun clear()
}