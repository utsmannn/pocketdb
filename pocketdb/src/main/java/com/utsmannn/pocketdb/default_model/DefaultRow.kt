package com.utsmannn.pocketdb.default_model

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DefaultRow<T>(private val data: T?, private val typeToken: TypeToken<T>) {
    fun getDefault() = data
    fun getType(): Type = typeToken.type
}