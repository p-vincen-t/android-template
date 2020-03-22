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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import co.app.common.dsl.dialog
import co.app.common.dsl.showProgress
import co.app.common.dsl.startActivity
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.*

/**
 * This base activity unifies calls to attachBaseContext as described in:
 * https://developer.android.com/guide/app-bundle/playcore#invoke_splitcompat_at_runtime
 */

abstract class BaseSplitActivity : BaseActivity() {

    private var onInstalled: ((String) -> Unit)? = null

    private val manager: SplitInstallManager by lazy {
        SplitInstallManagerFactory.create(this)
    }
    /** Listener used to handle changes in state for install requests. */
    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        val langsInstall = state.languages().isNotEmpty()

        val names = if (langsInstall) {
            // We always request the installation of a single language in this sample
            state.languages().first()
        } else state.moduleNames().joinToString(" - ")

        if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
            //  In order to see this, the application has to be uploaded to the Play Store.
            displayLoadingState(state, getString(R.string.downloading, names))
        } else if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
            /*
                      This may occur when attempting to download a sufficiently large module.

                      In order to see this, the application has to be uploaded to the Play Store.
                      Then features can be requested until the confirmation path is triggered.
                     */
            manager.startConfirmationDialogForResult(
                state, this,
                CONFIRMATION_REQUEST_CODE
            )
        } else if (state.status() == SplitInstallSessionStatus.INSTALLED) {
            if (langsInstall) {
                onSuccessfulLanguageLoad(names)
            } else {
                app.registerModule(names)
                onSuccessfulLoad(names)
            }
        } else if (state.status() == SplitInstallSessionStatus.INSTALLING) displayLoadingState(
            state,
            getString(R.string.installing, names)
        )
        else if (state.status() == SplitInstallSessionStatus.FAILED) {
            dialog(
                "Error installing module",
                getString(R.string.error_for_module, state.errorCode())
            )
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val ctx = newBase?.let { LanguageHelper.getLanguageConfigurationContext(it) }
        super.attachBaseContext(ctx)
        SplitCompat.install(this)
    }

    override fun onResume() {
        // Listener can be registered even without directly triggering a download.
        manager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        // Make sure to dispose of the listener once it's no longer needed.
        manager.unregisterListener(listener)
        super.onPause()
    }

    fun startAuthActivity() {
        onInstalled = {
            startActivity(App.AUTH_ACTIVITY)
            onInstalled = null
        }
        loadAndLaunchModule(App.AUTH_FEATURE_NAME)
    }

    /**
     * Load a feature by module name.
     * @param name The name of the feature module to load.
     */
    fun loadAndLaunchModule(name: String) {
        if (manager.installedModules.contains(name)) {
            dialog(getString(R.string.already_installed), "")
            onSuccessfulLoad(name)
            return
        }

        dialog(getString(R.string.loading_module, name), "")
        // Skip loading if the module already is installed. Perform success action directly.

        // Create request to install a feature module by name.
        val request = SplitInstallRequest.newBuilder()
            .addModule(name)
            .build()

        // Load and install the requested feature module.
        manager.startInstall(request)

        showProgress(getString(R.string.starting_install_for, name))
    }

    /**
     * Load language splits by language name.
     * @param lang The language code to load (without the region part, e.g. "en", "fr" or "pl").
     */
    private fun loadAndSwitchLanguage(lang: String) {
        showProgress(getString(R.string.loading_language, lang))
        // Skip loading if the language is already installed. Perform success action directly.
        if (manager.installedLanguages.contains(lang)) {
            dialog(getString(R.string.already_installed), "")
            onSuccessfulLanguageLoad(lang)
            return
        }

        // Create request to install a language by name.
        val request = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SplitInstallRequest.newBuilder()
                .addLanguage(Locale.forLanguageTag(lang))
                .build()
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }

        // Load and install the requested language.
        manager.startInstall(request)

        showProgress(getString(R.string.starting_install_for, lang))
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.setPackage(BuildConfig.APPLICATION_ID)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    }

    private fun onSuccessfulLoad(moduleName: String) {
        onInstalled?.invoke(moduleName)
    }

    private fun onSuccessfulLanguageLoad(lang: String) {
        LanguageHelper.language = lang
        recreate()
    }

    /** Display a loading state to the account. */
    private fun displayLoadingState(state: SplitInstallSessionState, message: String) {
        val bar = showProgress(message)
        bar.max = state.totalBytesToDownload().toInt()
        bar.progress = state.bytesDownloaded().toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            // Handle the account's decision. For example, if the account selects "Cancel",
            // you may want to disable certain functionality that depends on the module.
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, R.string.user_cancelled, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val CONFIRMATION_REQUEST_CODE = 1
    }

}