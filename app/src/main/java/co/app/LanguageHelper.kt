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

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.*

internal const val LANG_EN = "en"

internal const val LANG_PL = "pl"

object LanguageHelper {
    lateinit var prefs: SharedPreferences
    var language: String
        get() = prefs.getString("language", LANG_EN)!!
        set(value) = prefs.edit().putString(LANG_EN, value).apply()

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences(
            App.TEMP_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
    }

    /**
     * Get a Context that overrides the language selection in the Configuration instance used by
     * getResources() and getAssets() by one that is stored in the LanguageHelper preferences.
     *
     * @param ctx a base context to base the new context on
     */
    fun getLanguageConfigurationContext(ctx: Context): Context {
        val conf = getLanguageConfiguration()
        return ctx.createConfigurationContext(conf)
    }

    /**
     * Get an empty Configuration instance that only sets the language that is
     * stored in the LanguageHelper preferences.
     * For use with Context#createConfigurationContext or Activity#applyOverrideConfiguration().
     */
    private fun getLanguageConfiguration(): Configuration {
        val conf = Configuration()
        conf.setLocale(Locale.forLanguageTag(language))
        return conf
    }
}