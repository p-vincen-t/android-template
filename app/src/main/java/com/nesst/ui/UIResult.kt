package com.nesst.ui

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class UIResult<out T : Any> {

    class Success<out T : Any>(val data: T) : UIResult<T>() {
        /**
         * any extra info to be passed together with the data
         */
        var args: Any? = null
            /**
             *  if the info is null return new object
             */
            get() = field ?: Any()
            /**
             * if the info is null pass new object to the ergs
             */
            set(value) {
                field = value ?: Any()
            }
    }

    class Error<out E : Exception>(val exception: E) : UIResult<E>() {
        /**
         * any extra info to be passed together with the data
         */
        var args: Any? = null
            /**
             *  if the info is null return new object
             */
            get() = field ?: Any()
            /**
             * if the info is null pass new object to the ergs
             */
            set(value) {
                field = value ?: Any()
            }
    }

    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data, args=$args]"
        is Error -> "Error[exception=$exception, ergs=$args]"
    }
}

