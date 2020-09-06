package com.utsmannn.pocketdb.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.utsmannn.pocketdb.Pocket
import com.utsmannn.pocketdb.extensions.defaultCollectionOf
import com.utsmannn.pocketdb.extensions.defaultOf
import com.utsmannn.pocketdb.extensions.listen
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@FlowPreview
class MainActivity : AppCompatActivity() {

    data class SampleData(
        var name: String = "lah",
        var list: MutableList<String> = mutableListOf()
    )

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val simpleData = SampleData(
            name = "gua gantenk"
        )

        val list: MutableList<SampleData> = mutableListOf()
        list.add(simpleData)

        val default = defaultCollectionOf(emptyList<SampleData>())
        Pocket.collection("oke-collection")
            .flowOf("sample", default)
            .listen {
                tx_log.text = it.toString()
            }

        val defaultD = defaultOf(simpleData)
        Pocket.row("nn").insert("key", simpleData)
        Pocket.row("bbb").flowOf("key", defaultD)
            .listen {

            }

        GlobalScope.launch {
            Pocket.row("bb")
                .flowOf("key", defaultD)
                .collect {

                }
        }

        Pocket.row("bb").destroy("utsman")

        //Pocket.row("nn").selectOf()

        btn_add.setOnClickListener {
            Pocket.collection("oke-collection")
                .insert("sample", simpleData)
        }

        btn_remove.setOnClickListener {
            val currentList = Pocket.collection("oke-collection")
                .selectOf("sample", defaultCollectionOf(emptyList<SampleData>())).toMutableList()
            currentList.remove(simpleData)

            Pocket.collection("oke-collection").destroy("sample")
            Pocket.collection("oke-collection")
                .insertAll("sample", currentList)
        }
    }
}

