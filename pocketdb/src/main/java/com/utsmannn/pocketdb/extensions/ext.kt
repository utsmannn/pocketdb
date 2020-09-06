package com.utsmannn.pocketdb.extensions

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.utsmannn.pocketdb.default_model.DefaultCollection
import com.utsmannn.pocketdb.default_model.DefaultRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun logi(msg: String) = Log.i("pocket_logger", msg)

inline fun <reified T> Flow<T>.listen(crossinline data: (T) -> Unit) {
    GlobalScope.launch {
        collect {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    data.invoke(it)
                } catch (e: ClassCastException) {
                    throw IllegalArgumentException("Value cannot handle with ${T::class.java.name}")
                }
            }
        }
    }
}

inline fun <reified T> defaultOf(data: T?): DefaultRow<T> {
    return DefaultRow(data, typeToken = object : TypeToken<T>() {
    })
}

inline fun <reified T> defaultCollectionOf(data: Collection<T>): DefaultCollection<T> {
    return DefaultCollection(data, typeToken = object : TypeToken<Collection<T>>() {
    })
}

internal fun <T> T.convertToString(gson: Gson): String {
    return gson.toJson(this, object : TypeToken<T>() {}.type)
}

internal fun <T> T.convertCollectionToString(gson: Gson): String {
    return gson.toJson(this, object : TypeToken<Collection<T>>() {}.type)
}