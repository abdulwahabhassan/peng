package com.peng.model

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val rating: Int,
) {
    companion object {
        val products = listOf<Product>(
            Product(
                id = "1",
                name = "Facial Cleanser",
                description = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                price = 9.99,
                image = "",
                rating = 85
            ),
            Product(
                id = "2",
                name = "Hand Sanitizer",
                description = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                price = 4.50,
                image = "",
                rating = 64
            ),
            Product(
                id = "3",
                name = "Louis Body Deodorant",
                description = "Makes you shine brighter than the sun",
                price = 10.99,
                image = "",
                rating = 39
            ),
            Product(
                id = "4",
                name = "Louis Body Deodorant",
                description = "Size: 7.60fl oz/ 225ml Size: 7.60fl oz/ 225ml Pajbjklsbklja jbkajbkjs kjbaksjbja kbkajbsjkbajklbjbas kjbksjbjasbj kajbkjabs kjsbjka",
                price = 20.99,
                image = "",
                rating = 95
            ),
            Product(
                id = "5",
                name = "Louis Body Deodorant",
                description = "Makes you shine",
                price = 209.99,
                image = "",
                rating = 10
            )

        )
    }
}
