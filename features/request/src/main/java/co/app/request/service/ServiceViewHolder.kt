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

package co.app.request.service

import android.view.View
import co.app.request.R
import co.app.request.domain.service.Service
import promise.commons.data.log.LogUtil
import promise.ui.Viewable

class ServiceViewHolder(val service: Service): Viewable {

    override fun layout(): Int = R.layout.service

    override fun bind(view: View?, args: Any?) {
        LogUtil.e("service tag", "sku ", service.name)
    }

    override fun init(view: View?) {
    }

}