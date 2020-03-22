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

package co.app.auth.base

import co.app.auth.domain.LoginRequest
import co.app.auth.domain.RegistrationRequest
import co.app.auth.domain.Session
import co.app.common.account.UserAccount
import co.app.common.errors.AuthError
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import promise.commons.AndroidPromise
import promise.commons.tx.PromiseResult
import javax.inject.Inject

@SessionScope
class SessionImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userAccount: UserAccount,
    private val promise: AndroidPromise,
    private val compositeDisposable: CompositeDisposable
) : Session {

    override fun login(
        loginRequest: LoginRequest,
        result: PromiseResult<Boolean, in AuthError>
    ) {
        compositeDisposable.add(
            authApi.login(loginRequest)
                .observeOn(Schedulers.from(promise.executor()))
                .subscribe {
                    userAccount.create(it.body()!!)
                    result.response(true)
                }
        )
    }

    override fun resetPassword(
        resetPasswordRequest: String,
        result: PromiseResult<Boolean, in AuthError>
    ) {
        compositeDisposable.add(
            authApi.resetPassword(resetPasswordRequest)
                .observeOn(Schedulers.from(promise.executor()))
                .subscribe {
                    userAccount.create(it.body()!!)
                    result.response(true)
                }
        )
    }

    override fun register(
        registrationRequest: RegistrationRequest,
        result: PromiseResult<Boolean, in AuthError>
    ) {

    }

}