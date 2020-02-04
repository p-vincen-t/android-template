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

package com.app.dashboard

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.app.R
import com.app.domain.session.Account
import promise.ui.model.Viewable

class NavigationAccountViewHolder(private val account: Account) : Viewable {

    lateinit var accountImageView: AppCompatImageView

    override fun layout(): Int = R.layout.account_nav_layout

    override fun bind(view: View, args: Any?) {

    }

    override fun init(view: View) {
        accountImageView = view.findViewById(R.id.account_imageView)
    }
}