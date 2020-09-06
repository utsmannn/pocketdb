package com.utsmannn.pocketdb.app

import android.app.Application
import com.utsmannn.pocketdb.Pocket

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Pocket.init(this, "utsmangantenkyah")
        //Pocket.init(this, "utsman")

        /*startKoin {
            androidContext(this@MainApplication)
            Pocket.installKoinModule(this, "utsmangantenkyah")
        }*/
    }
}