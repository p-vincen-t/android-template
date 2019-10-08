package com.nesst.ui

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {
    /**
     * Success when there's data returned
     * @param T type of the data
     * @property data
     */
    data class Success<out T : Any>(val data: T) : Result<T>() {
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

    /**
     * Error when there's no data
     * @param E
     * @property exception
     */
    data class Error<out E : Exception>(val exception: E) : Result<E>() {
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

    /**
     *
     *
     * @return
     */
    override fun toString(): String = when (this) {
        is Success<*> -> "Success[data=$data, args=$args]"
        is Error -> "Error[exception=$exception, ergs=$args]"
    }
}
