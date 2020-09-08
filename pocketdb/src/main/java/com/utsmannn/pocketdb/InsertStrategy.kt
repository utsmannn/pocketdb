package com.utsmannn.pocketdb

sealed class InsertStrategy {
    object Override : InsertStrategy()
    object Ignore: InsertStrategy()
}