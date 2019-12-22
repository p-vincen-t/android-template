package com.nesstbase.auth

import promise.commons.model.Identifiable


class Account(val names: String, val email: String): Identifiable<String> {




    override fun setId(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}