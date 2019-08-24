package com.nesst.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.nesst.App

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private var hasBackButton = false
    lateinit var app: App
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as App
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        supportActionBar?.elevation = 0f
        if (isHasBackButton()) {
            val actionBar = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun isHasBackButton(): Boolean = hasBackButton

    fun setHasBackButton(hasBackButton: Boolean) {
        this.hasBackButton = hasBackButton
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    fun doAfterAnother(before: Runnable, after: Runnable) {
        handler.post(before)
        handler.postDelayed(after, 500)
    }

}