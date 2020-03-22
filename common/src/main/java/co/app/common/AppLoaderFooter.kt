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

package co.app.common

import android.view.View
import androidx.annotation.StringRes
import co.app.common.R
import co.app.common.dsl.getString
import promise.ui.loading.LoadingViewable

class AppLoaderFooter : LoadingViewable {
    private var message: String? = null

    constructor(message: String) {
        this.message = message
    }

    constructor(@StringRes resId: Int) {
        this.message = getString(resId)
    }

    override fun layout(): Int {
        return R.layout.progress_bar_layout
    }

    override fun init(view: View?) {

    }
}