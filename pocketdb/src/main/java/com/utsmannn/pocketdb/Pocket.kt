package com.utsmannn.pocketdb

import android.content.Context
import com.utsmannn.pocketdb.kind.PocketCollection
import com.utsmannn.pocketdb.kind.PocketRow
import org.koin.core.KoinApplication

object Pocket {
    fun init(context: Context, key: String) {
        PocketDb.install(context, key)
    }

    fun installKoinModule(koinApplication: KoinApplication, key: String) {
        PocketDb.installModule(koinApplication, key)
    }

    fun row(name: String): PocketRow {
        return PocketRow(name)
    }

    fun collection(name: String): PocketCollection {
        return PocketCollection(name)
    }
}