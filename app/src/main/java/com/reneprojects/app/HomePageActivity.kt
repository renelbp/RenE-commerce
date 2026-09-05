package com.reneprojects.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.reneprojects.core.feature.products.repository.ProductRepository
import com.reneprojects.feature.products.ui.PageContent
import com.reneprojects.feature.products.viewmodel.ProductViewModelImpl
import com.reneprojects.ui.theme.RenEcommerceTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
internal class HomePageActivity : ComponentActivity() {
    @Inject
    lateinit var productRepository: ProductRepository
    private val viewModel: ProductViewModelImpl by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            RenEcommerceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageContent(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PageContentPreview() {
//    RenEcommerceTheme {
//        PageContent("Android")
//    }
//}