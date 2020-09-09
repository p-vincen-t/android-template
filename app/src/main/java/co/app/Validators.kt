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

package co.app

import android.util.Patterns

fun isEmailValid(input: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(input).matches()

fun isPhoneNumberValid(input: String) = Patterns.PHONE.matcher(input).matches()

fun isIdentifierValid(input: String): Pair<Boolean, String?> =
    if (input.isBlank()) Pair(false, "Email or phone number is required")
    else if (input.contains('@')) {
        if (isEmailValid(input)) Pair(true, null)
        else Pair(false, "Your email address seems incorrect")
    } else {
        if (isPhoneNumberValid(input)) Pair(true, null)
        else Pair(false, "Your phone number seems incorrect")
    }

// A placeholder password validation check
fun isPasswordValid(password: String): Boolean = password.length > 5