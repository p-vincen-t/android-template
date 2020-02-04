/*
 * Copyright 2020, {{App}}
 * Licensed under the Apache License, Version 2.0, "{{App}} Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.domain.session.Session
import com.app.auth.AuthScope
import promise.commons.Promise
import javax.inject.Inject

/**
 * ViewModel provider factory to instantiate RegisterViewModel.
 * Required given RegisterViewModel has a non-empty constructor
 */
@AuthScope
class LoginViewModelFactory @Inject constructor() : ViewModelProvider.Factory {

    @Inject
    lateinit var session: Session
    @Inject
    lateinit var promise: Promise

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
            session = session,
            promise = promise
        ) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class")
    }
}
