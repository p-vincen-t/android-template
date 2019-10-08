package com.nesst.messaging.ui

import android.os.Bundle
import com.nesst.messaging.R
import com.nesst.ui.BaseSplitActivity


class MainActivity : BaseSplitActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addBackButton()
    }

}
