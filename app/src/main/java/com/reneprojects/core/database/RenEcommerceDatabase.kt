package com.reneprojects.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reneprojects.core.common.cachemanager.dao.CacheMetadataDao
import com.reneprojects.core.common.cachemanager.entity.CacheMetaDataEntity
import com.reneprojects.core.feature.products.local.dao.ProductDao
import com.reneprojects.core.feature.products.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        CacheMetaDataEntity::class
    ],
    version = 1,
    exportSchema = true
)
internal abstract class RenEcommerceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cacheMetadataDao(): CacheMetadataDao
}