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
            logi("offer --> ${getString(key, default)}")
            offer(getString(key, default.encrypt(secret))?.decrypt(secret))

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                if (key == k) {
                    logi("changed --> ${getString(key, default)}")
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
    internal fun <T> insert(data: T) =
        pref.edit().putString(key, data.convertToString(gson).encrypt(secret)).apply()

    /**
     * observing value with flow preferences
     * @param default is default item
     * @param type is generic type of class
     * */
    internal fun <T> select(default: T, type: Type): Flow<T?> {
        return pref.observeKey(default.convertToString(gson)).map {
            return@map try {
                logi("mmmm -> $it")
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
            logi("raw => $stringRaw")
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
            logi("key is -> $key")
            logi("observing list ----> $it")
            return@map try {
                gson.fromJson<Collection<T>>(it, type)
            } catch (e: JsonSyntaxException) {
                logi("taee")
                e.printStackTrace()
                throw IllegalArgumentException("Wrong key of existing data, key of data: $key")
            }
        }
    }

    internal fun <T> insertCollectionItem(data: T, type: Type) {
        val currentCollection = selectSingleCollection<T>(type)
        val newCollection = currentCollection.toMutableList().apply {
            add(data)
        }
        val stringCollection = gson.toJson(newCollection, type).encrypt(secret)
        pref.edit().putString(key, stringCollection).apply()
        logi("inserting -> $currentCollection")
    }

    internal fun <T> removeCollectionItem(data: T, type: Type, predicate: (T) -> Boolean) {
        val currentCollection = selectSingleCollection<T>(type)
        val newCollection = currentCollection.toMutableList().apply {
            logi("cuuuuuuuk -> $this")
            val dataFound = this.find(predicate)
            logi("data found --> $dataFound")
            remove(dataFound)

            //remove(data)
            /*val dataString = data.convertToString(gson)
            val stringData = gson.toJson(data, type)*/
            //logi("collection ---> $this -- data ---> $stringData")
            /*if (contains(data)) {
                logi("contains data, removing....")
                remove(data)
            } else {
                logi("not contains")
            }*/
        }
        val stringCollection = gson.toJson(newCollection, type).encrypt(secret)
        pref.edit().putString(key, stringCollection).apply()
    }

    internal fun <T> insertCollections(data: Collection<T>, type: Type) {
        val currentCollection = selectSingleCollection<T>(type)
        val newCollection = currentCollection.toMutableList().apply {
            addAll(data)
        }
        val stringCollection = gson.toJson(newCollection, type).encrypt(secret)
        pref.edit().putString(key, stringCollection).apply()
        logi("inserting -> $currentCollection")
    }

    // ------ END OF COLLECTION ------ //

    internal fun clear(key: String = "") {
        val allKeys = pref.all.keys
        if (key.isEmpty()) {
            logi("clear ----------")
            allKeys.forEach {
                pref.edit().remove(it).apply()
            }
        } else {
            logi("remove ----------")
            pref.edit().remove(key).apply()
        }
    }

}