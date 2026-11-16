package com.reneprojects.core.feature.categories.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.reneprojects.core.feature.categories.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories")
    suspend fun deleteCategories()

    @Transaction
    suspend fun replaceCategories(categories: List<CategoryEntity>) {
        deleteCategories()
        insertCategories(categories = categories)
    }
}
