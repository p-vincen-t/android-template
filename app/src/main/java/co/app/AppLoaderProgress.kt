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

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import co.app.R
import co.app.dsl.getString
import promise.ui.Viewable

class AppLoaderProgress : Viewable {
    private var message: String? = null

    private lateinit var textView: TextView

    constructor(message: String) {
        this.message = message
    }

    constructor(@StringRes resId: Int) {
        this.message = getString(resId)
    }

    override fun layout(): Int {
        return R.layout.progress_bar_layout
    }

    override fun bind(view: View?, args: Any?) {
        textView.text = message
    }

    override fun init(view: View) {
        textView = view.findViewById(R.id.progress_text)
    }
}