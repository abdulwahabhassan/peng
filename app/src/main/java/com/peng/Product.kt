package com.peng

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String
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
                image = ""
            ),
            Product(
                id = "2",
                name = "Hand Sanitizer",
                description = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                price = 4.50,
                image = ""
            ),
            Product(
                id = "3",
                name = "Louis Body Deodorant",
                description = "Makes you shine brighter than the sun",
                price = 10.99,
                image = ""
            ),
            Product(
                id = "4",
                name = "Louis Body Deodorant",
                description = "Makes you shine, better than his exes",
                price = 20.99,
                image = ""
            ),
            Product(
                id = "5",
                name = "Louis Body Deodorant",
                description = "Makes you shine",
                price = 209.99,
                image = ""
            )

        )
    }
}
