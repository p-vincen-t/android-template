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

package co.app.settings

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.app.BaseActivity
import co.app.R

class SettingsActivity : BaseActivity() {

    lateinit var themePreferenceRepo: ThemePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        addBackButton()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        themePreferenceRepo = app.themePreferenceRepo
        themePreferenceRepo
            .nightModeLive
            .observe(this, Observer {
                themePreferenceRepo.setTheme()
            })
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val themePreference: ListPreference? = findPreference("theme")
            val themeArrayOptionsAboveQ = resources.getStringArray(R.array.theme_options_above_q)
            val themeArrayOptionsBelowQ = resources.getStringArray(R.array.theme_options_below_q)
            themePreference?.entries =
                if (Build.VERSION.SDK_INT >= 29) themeArrayOptionsAboveQ else themeArrayOptionsBelowQ
            val themeArrayValues = resources.getStringArray(R.array.theme_values)
            themePreference?.summaryProvider =
                Preference.SummaryProvider<ListPreference> { preference ->
                    when (preference.value) {
//                 Light Theme
                        themeArrayValues[0] -> themeArrayOptionsAboveQ[0]
//                 Dark Theme
                        themeArrayValues[1] -> themeArrayOptionsAboveQ[1]
//                 System Defined Theme/Auto Battery
                        themeArrayValues[2] -> if (Build.VERSION.SDK_INT >= 29) themeArrayOptionsAboveQ[2] else themeArrayOptionsBelowQ[2]
                        else -> getString(R.string.def)
                    }
                }
        }

    }
}