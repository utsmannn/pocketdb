package com.utsmannn.pocketdb.kind

import android.app.Application
import com.google.gson.Gson
import com.utsmannn.pocketdb.InsertStrategy
import com.utsmannn.pocketdb.PocketDb
import com.utsmannn.pocketdb.PocketPreferences
import com.utsmannn.pocketdb.default_model.DefaultRow
import kotlinx.coroutines.flow.Flow
import org.koin.android.ext.android.inject

class PocketRow(private val name: String) : Application() {
    private val pocketDb: PocketDb by inject()
    private val gson: Gson by inject()

    fun <T> insert(key: String, data: T, insertStrategy: InsertStrategy = InsertStrategy.Ignore) = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).insert(data, insertStrategy)
    }

    fun <T> flowOf(key: String, defaultRow: DefaultRow<T>): Flow<T?> = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).select(
            defaultRow.getDefault(),
            defaultRow.getType()
        )
    }

    fun <T> selectOf(key: String, defaultRow: DefaultRow<T>): T? = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).selectSingle(
            defaultRow.getDefault(),
            defaultRow.getType()
        )
    }

    fun destroy(key: String = "") = run {
        PocketPreferences(key, pocketDb.pref(name), gson, pocketDb.getKey()).clear(key)
    }
}