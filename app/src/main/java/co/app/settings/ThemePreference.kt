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

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import promise.commons.pref.Preferences

class ThemePreference {

    private val sharedPreferences: Preferences by lazy {
        Preferences.Builder().EMPTY_STRING(PREFERENCE_THEME_DEF_VAL)
            .build()
            .preferenceChange(object: Preferences.PreferenceChange {
                override fun onChange(preferences: SharedPreferences?, key: String?) {
                    when (key) {
                        PREFERENCE_THEME_KEY -> {
                            _nightModeLive.value = _appTheme
                        }
                    }
                }
            })
    }

    fun setTheme() {
        nightModeLive.value.let { theme ->
            when (theme) {
                "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "System" ->
                    if (Build.VERSION.SDK_INT >= 29)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }

    private val _appTheme: String?
        get() = sharedPreferences.getString(PREFERENCE_THEME_KEY)

    private val _nightModeLive: MutableLiveData<String> = MutableLiveData()
    val nightModeLive: LiveData<String>
        get() = _nightModeLive

    init {
        _nightModeLive.value = _appTheme
    }

    companion object {
        private const val PREFERENCE_THEME_KEY = "theme"
        private const val PREFERENCE_THEME_DEF_VAL = "System"
    }
}