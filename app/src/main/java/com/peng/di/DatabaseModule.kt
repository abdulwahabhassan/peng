package com.peng.di

import android.content.Context
import com.peng.db.CartItemLocalDao
import com.peng.db.Database
import com.peng.db.FavouriteItemLocalDao
import com.peng.db.PaymentCardLocalDao
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
    fun provideCartItemLocalDao(
        @ApplicationContext appContext: Context
    ): CartItemLocalDao {
        return Database.getInstance(appContext).cartItemLocalDao()
    }

    @Provides
    @Singleton
    fun provideFavouriteItemLocalDao(
        @ApplicationContext appContext: Context
    ): FavouriteItemLocalDao {
        return Database.getInstance(appContext).favouriteItemLocalDao()
    }

    @Provides
    @Singleton
    fun providePaymentCardLocalDao(
        @ApplicationContext appContext: Context
    ): PaymentCardLocalDao {
        return Database.getInstance(appContext).paymentCardLocalDao()
    }

}