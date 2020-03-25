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

package co.app.auth.login

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.app.*
import co.app.common.errors.AuthError
import co.app.auth.domain.LoginRequest
import co.app.auth.domain.Session
import promise.commons.AndroidPromise
import promise.commons.tx.PromiseResult

class LoginViewModel(private val session: Session, private val promise: AndroidPromise) : BaseViewModel(),
    ViewForm {

    private val _result = MutableLiveData<UIResult<*>>()

    val uIResult: LiveData<UIResult<*>> = _result

    override fun clear() {

    }

    var identifier: String = ""

    var password: String = ""

    @Bindable
    var identifierError: String = ""

    @Bindable
    var passwordError: String = ""

    @Bindable
    var dataValid: Boolean = true

    @Bindable
    var progressLoading: Boolean = false

    override fun validate(args: Any?): Boolean {
        val valid = isIdentifierValid(identifier)
        if (!valid.first) {
            identifierError = valid.second!!
            notifyChanges()
            return false
        }
        if (!isPasswordValid(password)) {
            passwordError = "Check your password"
            notifyChanges()
            return false
        }
        return true
    }

    fun loginButtonClicked(v: View) {
        if (validate(null)) {
            dataValid = false
            progressLoading = true
            notifyChanges()
            promise.execute {
                session.login(
                    LoginRequest().apply {
                        identifier = identifier
                        password = password
                    }, PromiseResult<Boolean, AuthError>()
                        .withCallback {
                            promise.executeOnUi {
                                _result.value = UIResult.Success<Boolean>(it)
                            }
                        }
                        .withErrorCallback {
                            promise.executeOnUi {
                                _result.value = UIResult.Error<AuthError>(it)
                                dataValid = true
                                notifyChanges()
                            }
                        })
            }
        }
    }
}
