package com.taro.coroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.taro.coroutinesample.databinding.ActivityCancelCoroutineBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class CancelCoroutineActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCancelCoroutineBinding.inflate(layoutInflater)
    }

    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        with(binding){
            btLoadDataWithTimeout.setOnClickListener {

                lifecycleScope.launch {
                    progressBar.visibility = View.VISIBLE
                    // 設定時間
                    withTimeout(3000){
                        tvResult.text = networkCall()
                    }
                    progressBar.visibility = View.GONE
                }

            }

            btLoadDataWithJob.setOnClickListener {

                job = lifecycleScope.launch {
                    try {
                        progressBar.visibility = View.VISIBLE
                        tvResult.text = networkCall()
                        progressBar.visibility = View.GONE
                    }catch (e:CancellationException){
                        tvResult.text = "已取消"
                        progressBar.visibility = View.GONE
                    }
                }

            }

            btCancel.setOnClickListener {
                job?.cancel()
            }

        }
    }

    // suspend 宣告為耗時的 Function
    private suspend fun networkCall(): String{
        // withContext 切執行緒
        // Dispatchers.IO = 耗時工作
        // Error: function must be suspend
        val data = withContext(Dispatchers.IO){
            delay(2000)
            Log.d("DEBUG_TARO", "networkCall: ${Thread.currentThread().name}")
            "Data"
        }

        return data
    }
}