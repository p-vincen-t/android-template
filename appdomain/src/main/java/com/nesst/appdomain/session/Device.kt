package com.nesst.appdomain.session

import promise.commons.model.Identifiable


data class Device(
    val name: String,
    val description: String,
    val active: Boolean,
    var macAddress: String
): Identifiable<Int> {
    /**
     * get the id from the instance
     *
     * @return
     */
    override fun getId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * set the id to the instance
     *
     * @param t id
     */
    override fun setId(t: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}