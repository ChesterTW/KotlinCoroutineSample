package com.taro.coroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.taro.coroutinesample.databinding.ActivityAsyncBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class AsyncActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAsyncBinding.inflate(layoutInflater)
    }
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){

            /**
             * 無同步執行，耗時 5000 ms。
             * */
            btLoadData.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.visibility = View.VISIBLE
                    val time =measureTimeMillis {
                        val data = networkCall()
                        val data2 = networkCall2()
                        Log.d("DEBUG_TARO", "initView: ${Thread.currentThread().name}")
                        tvResult.text = "$data, $data2"
                        progressBar.visibility = View.GONE
                    }
                    tvTime.text = "耗費 $time ms"
                    Log.d("DEBUG_TARO", "initView: 耗費 $time ms")
                }
            }

            /**
             * 同步執行，耗時 3000 ms。
             * */
            btAsyncLoadData.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.visibility = View.VISIBLE
                    val time =measureTimeMillis {
                        val data = async { networkCall() }
                        val data2 = async { networkCall2() }
                        Log.d("DEBUG_TARO", "initView: ${Thread.currentThread().name}")
                        tvResult.text = "${data.await()}, ${data2.await()}"
                        progressBar.visibility = View.GONE
                    }
                    tvTime.text = "耗費 $time ms"
                    Log.d("DEBUG_TARO", "initView: 耗費 $time ms")
                }
            }

            /**
             * 同步執行中，有資料需要等同步執行結束後，才能進行同步執行。
             * */
            btLoadAsyncData.setOnClickListener {
                lifecycleScope.launch {
                    progressBar.visibility = View.VISIBLE
                    val time =measureTimeMillis {
                        val data = async { networkCall() }
                        val data2 = async { networkCall2() }
                        Log.d("DEBUG_TARO", "initView: ${Thread.currentThread().name}")
                        val data3 = async { networkCall3("${data.await()}, ${data2.await()}") }
                        tvResult.text = data3.await()
                        progressBar.visibility = View.GONE
                    }
                    tvTime.text = "耗費 $time ms"
                    Log.d("DEBUG_TARO", "initView: 耗費 $time ms")
                }
            }
        }
    }

    /**
     * 網路請求 1 號，延遲 2000 毫秒。
     * */
    private suspend fun networkCall(): String {
        return withContext(Dispatchers.IO){
            delay(2000)
            "Data"
        }
    }

    /**
     * 網路請求 2 號，延遲 3000 毫秒。
     * */
    private suspend fun networkCall2(): String {
        return withContext(Dispatchers.IO){
            delay(3000)
            "Data2"
        }
    }

    /**
     * 網路請求 3 號，延遲 3000 毫秒。
     * param: string: 要輸出的字串
     * */
    private suspend fun networkCall3(string: String): String {
        return withContext(Dispatchers.IO){
            delay(3000)
            string
        }
    }
}