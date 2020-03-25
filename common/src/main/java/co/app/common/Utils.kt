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

package co.app.common

import android.os.Looper
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.toDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if ((obj == null && lastObj != null)
                || obj != lastObj) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })
    return distinctLiveData
}

fun <T> MutableLiveData<List<T>>.addValue(t: T) {
    val values = if (value != null) value.toMutableList() else mutableListOf<T>()
    values.add(t)
    if (Thread.currentThread() == Looper.getMainLooper().thread) value = values
    else postValue(values)
}

fun <T> MutableLiveData<List<T>>.addValue(t: List<T>) {
    val values = if (value != null) value.toMutableList() else mutableListOf<T>()
    values.addAll(t)
    if (Thread.currentThread() == Looper.getMainLooper().thread) value = values
    else postValue(values)
}

fun <K, V> Map<K, V>.toArrayMay(): ArrayMap<K, V> {
    val array = ArrayMap<K, V>()
    for((t, u) in this) array[t] = u
    return array
}

