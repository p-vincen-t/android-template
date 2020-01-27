/*
 * Copyright 2017, Nesst
 * Licensed under the Apache License, Version 2.0, "Nesst Inc".
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesst.appdomain.session

import com.nesst.appdomain.errors.AuthError
import promise.commons.model.Result

interface Session {
    fun user(): Account
    fun login(loginRequest: LoginRequest, result: Result<Account, in AuthError>)
    fun resetPassword(resetPasswordRequest: String, result: Result<Boolean, in AuthError>)
    fun register(registrationRequest: RegistrationRequest, result: Result<Boolean, in AuthError>)
}