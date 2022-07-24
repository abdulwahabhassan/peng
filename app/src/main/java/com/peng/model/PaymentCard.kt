package com.peng.model

import com.peng.db.PaymentCardEntity

data class PaymentCard(
    val id: Long,
    val cardType: String,
    val cardTitle: String,
    val cardNumber: String,
    val cardCVV: String,
    val cardExpiryMonth: Int,
    val cardExpiryYear: Int,
    val selected: Boolean = false
)

fun PaymentCard.mapToPaymentCardEntity(): PaymentCardEntity {
    return PaymentCardEntity(
        cardType = this.cardType,
        cardTitle = this.cardTitle,
        cardNumber = this.cardNumber,
        cardCVV = this.cardCVV,
        cardExpiryMonth = this.cardExpiryMonth,
        cardExpiryYear = this.cardExpiryYear
    )
}
