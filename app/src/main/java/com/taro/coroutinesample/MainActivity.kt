package com.taro.coroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.taro.coroutinesample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        with(binding){
            btLoadData.setOnClickListener{
                lifecycleScope.launch {
                    val data = networkCall()
                    Log.d("DEBUG_TARO", "initView: ${Thread.currentThread().name}")
                    tvResult.text = data
                }
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