package com.peng.db

import androidx.room.*
import com.peng.model.PaymentCard

@Dao
interface PaymentCardLocalDao {
    @Query("SELECT * FROM payment_card")
    suspend fun getAllPaymentCards(): List<PaymentCardEntity>

    @Query("SELECT * FROM payment_card WHERE card_number==:cardNumber")
    suspend fun getPaymentCard(cardNumber: String): PaymentCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentCard(item: PaymentCardEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePaymentCard(item: PaymentCardEntity)

    @Delete
    suspend fun removePaymentCard(item: PaymentCardEntity)
}