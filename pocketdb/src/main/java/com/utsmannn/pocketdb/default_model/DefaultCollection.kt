package com.utsmannn.pocketdb.default_model

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DefaultCollection<T>(private val data: Collection<T>?, private val typeToken: TypeToken<Collection<T>>) {
    fun getDefault() = data
    fun getType(): Type = typeToken.type
}