package com.peng.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peng.model.PaymentCard
import com.peng.model.PaymentCardOptions

@Entity(tableName = "payment_card")
data class PaymentCardEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "card_type")
    val cardType: String,
    val cardTitle: String,
    @ColumnInfo(name = "card_number")
    val cardNumber: String,
    @ColumnInfo(name = "card_cvv")
    val cardCVV: String,
    @ColumnInfo(name = "card_expiry_month")
    val cardExpiryMonth: Int,
    @ColumnInfo(name = "card_expiry_year")
    val cardExpiryYear: Int,
) {
    companion object {
        val paymentCardEntities = listOf<PaymentCardEntity>(
            PaymentCardEntity(
                1,
                PaymentCardOptions.MASTER_CARD.name,
                "First Bank",
                "9090983373310900",
                "101",
                1,
                2024
            ),
            PaymentCardEntity(
                2,
                PaymentCardOptions.VISA_CARD.name,
                "GT Bank",
                "3763092000922738",
                "337",
                9,
                2023
            ),
            PaymentCardEntity(
                2,
                PaymentCardOptions.VERVE_CARD.name,
                "Access Bank",
                "5060666666666666666",
                "123",
                12,
                2025
            )
        )
    }
}

fun PaymentCardEntity.mapToPaymentCard(): PaymentCard {
    return PaymentCard(
        cardType = this.cardType,
        cardTitle = this.cardTitle,
        cardNumber = this.cardNumber,
        cardCVV = this.cardCVV,
        cardExpiryMonth = this.cardExpiryMonth,
        cardExpiryYear = this.cardExpiryYear
    )
}