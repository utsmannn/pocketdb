package com.utsmannn.pocketdb.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.utsmannn.pocketdb.InsertStrategy
import com.utsmannn.pocketdb.Pocket
import com.utsmannn.pocketdb.extensions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@FlowPreview
class MainActivity : AppCompatActivity() {

    data class SampleData(
        var name: String = "lah",
        var list: MutableList<String> = mutableListOf()
    )

    fun logi(msg: String) = Log.i("pocket_logger_sample", msg)

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
        Pocket.collection("tae")
            .flowOf("sample", default)
            .listenOnUi {
                //tx_log.text = it.toString()
            }

        val allKeys = Pocket.collection("tae")
            .keys()
        tx_log.text = allKeys.toString()

        val defaultD = defaultOf(simpleData)
        //Pocket.row("nn").insert("key", simpleData)

        /*GlobalScope.launch {
            Pocket.row("bbb").flowOf("key", defaultD)
                .collect {
                    CoroutineScope(Dispatchers.Main).launch {
                        tx_log.text = "updating.."
                        delay(1000)
                        tx_log.text = it?.name
                    }

                }
        }*/

        /*GlobalScope.launch {
            Pocket.row("bb")
                .flowOf("key", defaultD)
                .collect {

                }
        }*/

        //Pocket.row("bb").destroy("utsman")

        //Pocket.row("nn").selectOf()

        btn_add.setOnClickListener {
            //Pocket.row("bbb").insert("key", simpleData, InsertStrategy.Override)
            Pocket.collection("tae")
                .insert("sample", simpleData)
        }

        btn_remove.setOnClickListener {
            /*val currentList = Pocket.collection("oke-collection")
                .selectOf("sample", defaultCollectionOf(emptyList<SampleData>())).toMutableList()
            currentList.remove(simpleData)

            Pocket.collection("oke-collection").destroy("sample")
            Pocket.collection("oke-collection")
                .insertAll("sample", currentList)*/

            //Pocket.row("bbb").insert("key", simpleData, InsertStrategy.Override)
            //Pocket.row("bbb").destroy("key")
            /*val editedList = Pocket.collection("oke-collection-sample")
                .selectOf("sample-2", defaultCollectionOf(emptyList<SampleData>()))
                .toMutableList()
                .apply {
                    remove(simpleData)
                }

            Pocket.collection("oke-collection-sample")
                .destroy("sample")
            Pocket.collection("oke-collection-sample")
                .insertAll("sample", editedList)*/

            /*Pocket.collection("oke-collection-sample")
                .destroy()*/
        }
    }
}

