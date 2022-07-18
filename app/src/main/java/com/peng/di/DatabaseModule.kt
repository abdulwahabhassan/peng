package com.peng.di

import android.content.Context
import com.peng.db.CartItemLocalDao
import com.peng.db.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVehicleLocalDAO(
        @ApplicationContext appContext: Context
    ): CartItemLocalDao {
        return Database.getInstance(appContext).cartItemLocalDao()
    }

}