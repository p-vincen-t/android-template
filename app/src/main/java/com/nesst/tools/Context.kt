package com.nesst.tools

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tapadoo.alerter.Alerter
import com.tbruyelle.rxpermissions2.RxPermissions
import promise.commons.util.Conditions

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
 *
 *
 * @param permissions
 * @param request
 */
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
fun Activity.requestPermission(
    callBack: (String, Boolean) -> Unit,
    permission: String
) {
    RxPermissions(this).request(permission)
        .subscribe { aBoolean -> callBack(permission, aBoolean) }
}

/**
 *
 *
 * @return
 */
fun Context.appVersionName(): String {
    return try {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        packageInfo.versionName.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
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
fun AppCompatSpinner.populate(items: List<String>, onSelected: ((Int) -> Unit)? = null, selected: String? = null) {
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