package com.peng.repo

import com.peng.db.PaymentCardEntity
import com.peng.db.PaymentCardLocalDao
import javax.inject.Inject

// Repository to manage payment cards
class PaymentCardRepository @Inject constructor(private val paymentCardLocalDao: PaymentCardLocalDao) {

    suspend fun getAllPaymentCards(): List<PaymentCardEntity> {
        return paymentCardLocalDao.getAllPaymentCards()
    }

    suspend fun getPaymentCard(cardNumber: String): PaymentCardEntity? {
        return paymentCardLocalDao.getPaymentCard(cardNumber)
    }

    suspend fun updatePaymentCard(item: PaymentCardEntity) {
        paymentCardLocalDao.updatePaymentCard(item)
    }

    suspend fun insertPaymentCard(item: PaymentCardEntity) {
        paymentCardLocalDao.insertPaymentCard(item)
    }

    suspend fun removePaymentCard(item: PaymentCardEntity) {
        paymentCardLocalDao.removePaymentCard(item)
    }
}