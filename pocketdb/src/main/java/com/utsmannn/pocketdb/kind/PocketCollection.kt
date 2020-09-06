package com.utsmannn.pocketdb.kind

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.utsmannn.pocketdb.PocketDb
import com.utsmannn.pocketdb.PocketPreferences
import com.utsmannn.pocketdb.default_model.DefaultCollection
import com.utsmannn.pocketdb.default_model.DefaultRow
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.inject

class PocketCollection(private val name: String) : Application() {
    private val pocketDb: PocketDb by inject()
    private val gson: Gson by inject()

    fun <T> insert(key: String, data: T) = run {
        val defaultData = DefaultCollection(emptyList(), object : TypeToken<Collection<T>>() {})
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).insertCollectionItem(
            data,
            defaultData.getType()
        )
    }

    fun <T> remove(key: String, data: T, predicate: (T) -> Boolean) = run {
        val defaultData = DefaultCollection(emptyList(), object : TypeToken<Collection<T>>() {})
        //val defaultData = DefaultRow(data, object : TypeToken<T>() {})
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).removeCollectionItem(
            data,
            defaultData.getType(),
            predicate
        )
    }

    fun <T> insertAll(key: String, data: Collection<T>) = run {
        val defaultData = DefaultCollection(emptyList(), object : TypeToken<Collection<T>>() {})
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).insertCollections(
            data,
            defaultData.getType()
        )
    }

    fun <T> flowOf(key: String, default: DefaultCollection<T>): Flow<Collection<T?>> = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).selectCollection(
            default.getDefault(),
            default.getType()
        )
    }

    fun <T> selectOf(key: String, default : DefaultCollection<T>): Collection<T> = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).selectSingleCollection(
            default.getType()
        )
    }

    fun destroy(key: String = "") = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).clear(key)
    }
}