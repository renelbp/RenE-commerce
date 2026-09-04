package com.reneprojects.core.di

import android.content.Context
import androidx.room.Room
import com.reneprojects.core.common.cachemanager.dao.CacheMetadataDao
import com.reneprojects.core.database.RenEcommerceDatabase
import com.reneprojects.core.feature.products.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RenEcommerceDatabaseModule {

    @Provides
    @Singleton
    fun provideRenEcommerceDatabase(
        @ApplicationContext context: Context
    ): RenEcommerceDatabase {
        return Room.databaseBuilder(
            context,
            RenEcommerceDatabase::class.java,
            "ecommerce_database"
        ).build()
    }

    @Provides
    fun provideProductDao(
        database: RenEcommerceDatabase
    ): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideCacheMetadataDao(
        database: RenEcommerceDatabase
    ): CacheMetadataDao {
        return database.cacheMetadataDao()
    }
}