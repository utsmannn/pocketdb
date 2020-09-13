package com.utsmannn.pocketdb

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.utsmannn.pocketdb.extensions.convertToString
import com.utsmannn.pocketdb.extensions.decrypt
import com.utsmannn.pocketdb.extensions.encrypt
import com.utsmannn.pocketdb.extensions.logi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

/**
 * Pocket engine using Shared Preferences
 * Encryption using AES and serialize using gson
 * @author Muhammad Utsman
 * */
internal class PocketPreferences(
    private val key: String?,
    private val pref: SharedPreferences,
    private val gson: Gson,
    private val secret: String
) {

    /**
     * Observing preferences changes use OnSharedPreferenceChangeListener
     * wrapping with Coroutine Flow
     * @param default is default of value preferences
     * @param dispatcher is coroutine dispatcher where listen the flow
     * */
    private fun SharedPreferences.observeKey(
        default: String,
        dispatcher: CoroutineContext = Dispatchers.Default
    ): Flow<String?> {
        val flow: Flow<String?> = channelFlow {
            offer(getString(key, default.encrypt(secret))?.decrypt(secret))

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                if (key == k) {
                    offer(getString(key, default.encrypt(secret))?.decrypt(secret))
                }
            }

            registerOnSharedPreferenceChangeListener(listener)
            awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
        }
        return flow.flowOn(dispatcher)
    }

    // ------ ROW ------ //
    /**
     * insert item to preferences
     * */
    internal fun <T> insert(data: T, strategy: InsertStrategy) {
        when (strategy) {
            is InsertStrategy.Override -> {
                pref.edit().remove(key).apply()
                pref.edit().putString(key, data.convertToString(gson).encrypt(secret)).apply()
            }
            is InsertStrategy.Ignore -> {
                pref.edit().putString(key, data.convertToString(gson).encrypt(secret)).apply()
            }
        }
    }

    /**
     * observing value with flow preferences
     * @param default is default item
     * @param type is generic type of class
     * */
    internal fun <T> select(default: T, type: Type): Flow<T?> {
        return pref.observeKey(default.convertToString(gson)).map {
            return@map try {
                gson.fromJson<T>(it, type)
            } catch (e: JsonSyntaxException) {
                throw IllegalArgumentException("Wrong key of existing data, key of data: $key")
            }
        }
    }

    /**
     * select single value without observing any event
     * @param default is default item
     * @param type is generic type of class
     * */
    internal fun <T> selectSingle(default: T, type: Type): T? {
        val defaultString = gson.toJson(default, type).encrypt(secret)
        val stringRaw = pref.getString(key, defaultString)?.decrypt(secret)
        return try {
            gson.fromJson<T>(stringRaw, type)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            throw IllegalArgumentException("Wrong key of existing data, key of data: $key")
        }
    }

    // ------ END OF ROW ------ //


    // ------ COLLECTION ------ //

    internal fun <T> selectSingleCollection(type: Type): Collection<T> {
        val default = emptyList<T>()
        val defaultString = gson.toJson(default, type).encrypt(secret)
        val stringRaw = pref.getString(key, defaultString)?.decrypt(secret)
        return if (stringRaw == null) {
            return emptyList()
        } else {
            gson.fromJson(stringRaw, type)
        }
    }

    internal fun <T> selectCollection(default: Collection<T>?, type: Type): Flow<Collection<T>> {
        val defaultString = gson.toJson(default, type)
        return pref.observeKey(defaultString).map {
            return@map try {
                gson.fromJson<Collection<T>>(it, type)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                throw IllegalArgumentException("Wrong key of existing data, key of data: $key")
            }
        }
    }

    internal fun <T> insertCollectionItem(data: T, type: Type, strategy: InsertStrategy) {
        val currentCollection = selectSingleCollection<T>(type)
        val newCollection = currentCollection.toMutableList().apply {
            add(data)
        }
        val stringCollection = gson.toJson(newCollection, type).encrypt(secret)

        when (strategy) {
            is InsertStrategy.Override -> {
                pref.edit().remove(key).apply()
                pref.edit().putString(key, stringCollection).apply()
            }
            is InsertStrategy.Ignore -> {
                pref.edit().putString(key, stringCollection).apply()
            }
        }
    }

    internal fun <T> insertCollections(data: Collection<T>, type: Type, strategy: InsertStrategy) {
        val currentCollection = selectSingleCollection<T>(type)
        val newCollection = currentCollection.toMutableList().apply {
            addAll(data)
        }
        val stringCollection = gson.toJson(newCollection, type).encrypt(secret)
        when (strategy) {
            is InsertStrategy.Override -> {
                pref.edit().remove(key).apply()
                pref.edit().putString(key, stringCollection).apply()
            }
            is InsertStrategy.Ignore -> {
                pref.edit().putString(key, stringCollection).apply()
            }
        }
    }

    internal fun removeItemCollection(data: Any, type: Type) {
        logi("removing....")
        val currentCollection = selectSingleCollection<Any>(type)
            .toMutableList().apply {
                remove(data)
            }
        insertCollections(currentCollection, type, InsertStrategy.Override)
    }

    // ------ END OF COLLECTION ------ //

    internal fun clear() {
        val allKeys = pref.all.keys
        val keyDecrypt = key?.decrypt(secret)
        if (keyDecrypt.isNullOrEmpty()) {
            allKeys.forEach {
                pref.edit().remove(it).apply()
            }
        } else {
            pref.edit().remove(key).apply()
        }
    }

}