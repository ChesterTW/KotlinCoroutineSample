package com.taro.coroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.taro.coroutinesample.databinding.ActivityJobBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityJobBinding.inflate(layoutInflater)
    }
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){

            btLoadData.setOnClickListener{
                job = lifecycleScope.launch {
                    try {
                        Log.d("DEBUG_TARO", "initView: 1")
                        tvResult.text = networkCall()
                        Log.d("DEBUG_TARO", "initView: 2")
                    }catch (e:CancellationException){
                        Log.d("DEBUG_TARO", "initView: Cancel Done.")
                    }
                }

                lifecycleScope.launch {
                    Log.d("DEBUG_TARO", "initView: 3")
                    job?.join()
                    Log.d("DEBUG_TARO", "initView: 4")
                }

            }

            btChildJob.setOnClickListener {
                job = lifecycleScope.launch {
                    try {
                        Log.d("DEBUG_TARO", "initView: 1")
                        tvResult.text = networkCall()
                        launch {
                            Log.d("DEBUG_TARO", "initView: A")
                            tvResult.text = networkCall2()
                            Log.d("DEBUG_TARO", "initView: B")
                        }
                        Log.d("DEBUG_TARO", "initView: 2")
                    }catch (e:CancellationException){
                        Log.d("DEBUG_TARO", "initView: Cancel Done")
                    }

                }

                lifecycleScope.launch {
                    job?.join()
                    Log.d("DEBUG_TARO", "initView: 3")
                }
            }

            btCancel.setOnClickListener {
//                job?.cancel()
                lifecycleScope.launch {
                    job?.cancelAndJoin()
                    Log.d("DEBUG_TARO", "initView: After cancel")
                }
            }

            btLoadLargeData.setOnClickListener {
                job = lifecycleScope.launch {
                    var i = 0
                    while (i<5){
                        delay(1000)
                        Log.d("DEBUG_TARO", "initView: $i")
                        i++
                    }
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

}