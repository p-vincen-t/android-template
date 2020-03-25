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

package co.app.dsl

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.StrictMode
import android.view.DisplayCutout
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import co.app.R
import com.tbruyelle.rxpermissions2.RxPermissions
import promise.commons.AndroidPromise
import promise.commons.util.Conditions


/** Combination of all flags required to put activity into immersive mode */
const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/** Milliseconds used for UI animations */
const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L

inline fun <reified T: AppCompatActivity> Context.startActivity(noinline intentBlock: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intentBlock.invoke(intent)
    this.startActivity(intent)
}

fun  Context.startActivity(className: String, intentBlock: Intent.() -> Unit = {}) {
    val intent = Intent()
    intent.setClassName(this.applicationContext, className)
    intentBlock.invoke(intent)
    this.startActivity(intent)
}

/** Launch an activity by its class name. */
@Deprecated("use start activity passing className",
    replaceWith = ReplaceWith("startActivity(className)"),
    level = DeprecationLevel.ERROR)
fun Context.launchActivity(className: String) {
    Intent().setClassName("co.app", className)
        .also {
            startActivity(it)
        }
}

fun getString(@StringRes resId: Int): String {
    val app = AndroidPromise.instance().context()
    return app.getString(resId)
}
/**
 *
 *
 * @property text
 * @property listener
 */
data class DialogButton(val text: String, val listener: ((DialogInterface) -> Unit)? = null) {
    fun executeListener(`interface`: DialogInterface) {
        listener?.invoke(`interface`)
    }
}

/**
 *
 *
 * @param title
 * @param message
 * @param positive
 * @param negative
 * @param neutral
 */
fun Context.dialog(
    title: String,
    message: String,
    positive: DialogButton? = null,
    negative: DialogButton? = null,
    neutral: DialogButton? = null
) {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
    if (positive != null) builder.setPositiveButton(
        positive.text
    ) { p0, _ ->
        positive.executeListener(p0)
    }
    if (negative != null) builder.setNegativeButton(
        negative.text
    ) { p0, _ ->
        negative.executeListener(p0)
    }
    if (neutral != null) builder.setNeutralButton(
        neutral.text
    ) { p0, _ ->
        neutral.executeListener(p0)
    }
}


/**
 * Simulate a button click, including a small delay while it is being pressed to trigger the
 * appropriate animations.
 */
fun ImageButton.simulateClick(delay: Long = ANIMATION_FAST_MILLIS) {
    performClick()
    isPressed = true
    invalidate()
    postDelayed({
        invalidate()
        isPressed = false
    }, delay)
}

/** Pad this view with the insets provided by the device cutout (i.e. notch) */
@RequiresApi(Build.VERSION_CODES.P)
fun View.padWithDisplayCutout() {

    /** Helper method that applies padding from cutout's safe insets */
    fun doPadding(cutout: DisplayCutout) = setPadding(
        cutout.safeInsetLeft,
        cutout.safeInsetTop,
        cutout.safeInsetRight,
        cutout.safeInsetBottom)

    // Apply padding using the display cutout designated "safe area"
    rootWindowInsets?.displayCutout?.let { doPadding(it) }

    // Set a listener for window insets since view.rootWindowInsets may not be ready yet
    setOnApplyWindowInsetsListener { _, insets ->
        insets.displayCutout?.let { doPadding(it) }
        insets
    }
}


/** Same as [AlertDialog.show] but setting immersive mode in the dialog's window */
fun AlertDialog.showImmersive() {
    // Set the dialog to not focusable
    window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)

    // Make sure that the dialog's window is in full screen
    window?.decorView?.systemUiVisibility = FLAGS_FULLSCREEN

    // Show the dialog while still in immersive mode
    show()

    // Set the dialog to focusable again
    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
}

fun Activity.showProgress(
    message: String,
    cancelable: Boolean = true,
    show: Boolean = true
): ProgressBar {
    val builder = AlertDialog.Builder(this)
    builder.setCancelable(cancelable)
    val view = this.layoutInflater.inflate(R.layout.progress_bar_layout, null)
    val textView = view.findViewById<TextView>(R.id.progress_text)
    textView.text = message
    builder.setView(view)
    val dialog = builder.create()
    if (show) dialog.show()
    else dialog.dismiss()
    return view.findViewById(R.id.progress_bar)
}

/**
 *
 *
 * @param permissions
 * @param request
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.requestPermission(
    permissions: Array<String>,
    request: Int
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    Conditions.checkNotNull(permissions)
    Conditions.checkState(permissions.isNotEmpty(), "No permissions requested")
    ActivityCompat.requestPermissions(
        this,
        permissions, request
    )
}

/**
 *
 *
 * @param callBack
 * @param permission
 */
@SuppressLint("CheckResult")
fun FragmentActivity.requestPermission(
    permission: String,
    callBack: (String, Boolean) -> Unit
) {
    RxPermissions(this).request(permission)
        .subscribe { aBoolean -> callBack(permission, aBoolean) }
}

/**
 *
 *
 * @return
 */
fun Context.appVersionName(): String = try {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    packageInfo.versionName.toString()
} catch (e: PackageManager.NameNotFoundException) {
    ""
}

/**
 *
 *
 * @param callBack
 * @param permissions
 * @param request
 */
fun Activity.checkPermissions(
    callBack: (ArrayList<String>, Boolean) -> Unit,
    permissions: Array<String>,
    request: Int
) {
    val passed = ArrayList<String>()
    val failed = ArrayList<String>()
    var checked = true
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            failed.add(permission)
            checked = false
        } else
            passed.add(permission)
    }
    if (checked) {
        if (passed.isNotEmpty())
            callBack(passed, true)
        else
            callBack(passed, false)
    } else {
        if (failed.isNotEmpty()) {
            val fails = arrayOfNulls<String>(failed.size)
            for (i in 0 until failed.size) fails[i] = failed[i]
            this.requestPermission(fails.requireNoNulls(), request)
        } else
            callBack(ArrayList(), false)
    }
}

/**
 *
 *
 * @param items
 * @param onSelected
 * @param selected
 */
fun AppCompatSpinner.populate(
    items: List<String>,
    onSelected: ((Int) -> Unit)? = null,
    selected: String? = null
) {
    val startAdapter = ArrayAdapter(this.context, android.R.layout.simple_spinner_item, items)
    startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (onSelected != null) onSelected(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
    this.adapter = startAdapter
    if (selected != null) {
        val index = items.indexOfFirst { it == selected }
        if (index == -1) return
        this.setSelection(index)
    }
}


/**
 *
 *
 */
@SuppressLint("ObsoleteSdkInt")
@TargetApi(11)
fun Class<*>.enableStrictMode() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
        val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            threadPolicyBuilder.penaltyFlashScreen()
            vmPolicyBuilder
                .setClassInstanceLimit(this, 1)
        }
        StrictMode.setThreadPolicy(threadPolicyBuilder.build())
        StrictMode.setVmPolicy(vmPolicyBuilder.build())
    }
}

fun Context.hasGPSProvider(): Boolean {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = manager.allProviders ?: return false
    return providers.contains(LocationManager.GPS_PROVIDER)
}