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

package co.app.common.account

import promise.commons.model.Identifiable

data class Device(
    val name: String,
    val description: String,
    val active: Boolean,
    var macAddress: String,
    var token: String = ""
): Identifiable<String> {
    /**
     * get the id from the instance
     *
     * @return
     */
    override fun getId(): String = macAddress

    /**
     * set the id to the instance
     *
     * @param t id
     */
    override fun setId(t: String) {
        throw IllegalAccessError("cant set device id")
    }
}