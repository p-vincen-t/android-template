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

package co.base.account

import co.app.common.account.Device

data class DeviceImpl(
    override val name: String,
    override val description: String,
    override val active: Boolean,
    override var macAddress: String,
    override var token: String
) : Device {
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
    override fun setId(t: String): Unit = throw IllegalAccessError("cant set device id")
}