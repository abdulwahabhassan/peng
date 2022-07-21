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
    val cardNumber: String
) {
    companion object {
        val paymentCardEntities = listOf<PaymentCardEntity>(
            PaymentCardEntity(
                1,
                PaymentCardOptions.VERVE_CARD.name,
                "First Bank",
                "9090 9833 7331 0900"
            ),
            PaymentCardEntity(
                2,
                PaymentCardOptions.VISA_CARD.name,
                "GT Bank",
                "3763 0920 0092 2738"
            ),
            PaymentCardEntity(
                2,
                PaymentCardOptions.MASTER_CARD.name,
                "Access Bank",
                "6273 1029 4643 9281"
            )
        )
    }
}

fun PaymentCardEntity.mapToPaymentCard(): PaymentCard {
    return PaymentCard(
        id = this.id,
        cardType = this.cardType,
        cardTitle = this.cardTitle,
        cardNumber = this.cardNumber
    )
}