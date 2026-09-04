package com.reneprojects.core.feature.products.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.reneprojects.core.feature.products.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ProductDao {
    @Query("SELECT * FROM products")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    suspend fun getProducts(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteProducts()

    @Transaction
    suspend fun replaceProducts(
        products: List<ProductEntity>
    ) {
        deleteProducts()
        insertProducts(products = products)
    }
}