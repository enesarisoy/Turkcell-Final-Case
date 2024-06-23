package com.ns.turkcellfinal

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.presentation.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getProducts()
        lifecycleScope.launch {
            viewModel.products.flowWithLifecycle(lifecycle).collect { response ->
                when (response) {
                    is ViewState.Loading -> {
                        // Show loading
                        println("Loading")
                    }

                    is ViewState.Success -> {
                        // Show data
                        val products = response.result as BaseResponse.Success

                        products.data.products.forEach {
                            println(it.title)
                        }
                    }

                    is ViewState.Error -> {
                        // Show error
                        println("Error -> ${response.error}")
                    }
                }
            }
        }
    }
}