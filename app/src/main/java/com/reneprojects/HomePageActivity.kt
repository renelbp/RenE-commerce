package com.reneprojects

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.reneprojects.ui.theme.RenEcommerceTheme

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var productList: List<Product> by remember { mutableStateOf(emptyList()) }
            LaunchedEffect(Unit) {
                try {
                    productList = RetrofitClient.retrofitService.getProductResponse().products
                } catch (e: Exception) {
                    Toast.makeText(
                        this@HomePageActivity, "Error trying to fetch Content, Cause: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            RenEcommerceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageContent(
                        productList = productList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun PageContent(productList: List<Product>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        items(items = productList, key = { it.id }) { product ->
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = product.thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = product.title, style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "$${product.price}", style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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