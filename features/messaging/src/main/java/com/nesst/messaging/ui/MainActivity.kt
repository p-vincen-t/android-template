package com.nesst.messaging.ui

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.nesst.messaging.R
import com.nesst.ui.BaseSplitActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseSplitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

}
