package com.peng.model

data class CartItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    var quantity: Int,
) {
    companion object {
        val cartItems = listOf<CartItem>(
            CartItem(
                id = "1",
                name = "Facial Cleanser",
                description = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                price = 9.99,
                image = "",
                quantity = 12
            ),
            CartItem(
                id = "2",
                name = "Hand Sanitizer",
                description = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                price = 4.50,
                image = "",
                quantity = 2
            ),
            CartItem(
                id = "3",
                name = "Louis Body Deodorant",
                description = "Makes you shine brighter than the sun",
                price = 10.99,
                image = "",
                quantity = 1
            ),
            CartItem(
                id = "4",
                name = "Louis Body Deodorant",
                description = "Size: 7.60fl oz/ 225ml Size: 7.60fl oz/ 225ml Pajbjklsbklja jbkajbkjs kjbaksjbja kbkajbsjkbajklbjbas kjbksjbjasbj kajbkjabs kjsbjka",
                price = 20.99,
                image = "",
                quantity = 5
            ),
            CartItem(
                id = "5",
                name = "Louis Body Deodorant",
                description = "Makes you shine",
                price = 209.99,
                image = "",
                quantity = 1
            )

        )
    }
}