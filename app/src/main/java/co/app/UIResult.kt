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

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class UIResult<out T : Any> {

    class Success<out T : Any>(val data: T) : UIResult<T>() {
        /**
         * any extra info to be passed together with the data
         */
        var args: Any? = null
            /**
             *  if the info is null return new object
             */
            get() = field ?: Any()
            /**
             * if the info is null pass new object to the ergs
             */
            set(value) {
                field = value ?: Any()
            }
    }

    class Error<out E : Throwable>(val exception: E) : UIResult<E>()

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data, args=$args]"
        is Error -> "Error[exception=$exception"
    }
}

