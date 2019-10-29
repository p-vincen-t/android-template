package com.nesst.messaging.ui

import android.os.Bundle
import com.nesst.messaging.R
import com.nesst.ui.BaseActivity

class ChatActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        addBackButton()
    }
}
