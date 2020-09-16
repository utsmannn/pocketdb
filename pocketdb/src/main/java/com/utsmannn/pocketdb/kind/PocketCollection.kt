package com.utsmannn.pocketdb.kind

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.utsmannn.pocketdb.InsertStrategy
import com.utsmannn.pocketdb.PocketDb
import com.utsmannn.pocketdb.PocketPreferences
import com.utsmannn.pocketdb.default_model.DefaultCollection
import com.utsmannn.pocketdb.extensions.decrypt
import com.utsmannn.pocketdb.extensions.encrypt
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.inject

class PocketCollection(private val name: String) : Application() {
    private val pocketDb: PocketDb by inject()
    private val gson: Gson by inject()

    fun <T> insert(key: String, data: T, insertStrategy: InsertStrategy = InsertStrategy.Ignore) = run {
        val defaultData = DefaultCollection(emptyList(), object : TypeToken<Collection<T>>() {})
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getSecretKey()).insertCollectionItem(
            data,
            defaultData.getType(),
            insertStrategy
        )
    }

    fun <T> insertAll(key: String, data: Collection<T>, insertStrategy: InsertStrategy = InsertStrategy.Ignore) = run {
        val defaultData = DefaultCollection(emptyList(), object : TypeToken<Collection<T>>() {})
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getSecretKey()).insertCollections(
            data,
            defaultData.getType(),
            insertStrategy
        )
    }

    fun <T> flowOf(key: String, default: DefaultCollection<T>): Flow<Collection<T?>> = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getSecretKey()).selectCollection(
            default.getDefault(),
            default.getType()
        )
    }

    fun <T> selectOf(key: String, default : DefaultCollection<T>): Collection<T> = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getSecretKey()).selectSingleCollection(
            default.getType()
        )
    }

    fun destroy(key: String = "") = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getSecretKey()).clear()
    }

    fun keys(): List<String> = run {
        pocketDb.pref(name).all.keys.toList()
    }
}