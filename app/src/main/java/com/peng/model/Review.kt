package com.peng.model

data class Review(
    val id: String,
    val name: String,
    val comment: String,
    val rating: Int,
    val image: String,
    val date: String = "Mon, 14 Feb 2022"
) {
    companion object {
        val reviews = listOf<Review>(
            Review(
                id = "1",
                name = "Mark Sam",
                comment = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                rating = 85,
                image = ""
            ),
            Review(
                id = "2",
                name = "Priscilla Donald Mike",
                comment = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                rating = 54,
                image = ""
            ),
            Review(
                id = "3",
                name = "Achimugu Osapa",
                comment = "Makes you shine brighter than the sun",
                rating = 77,
                image = ""
            ),
            Review(
                id = "4",
                name = "Cynthia Berry",
                comment = "I love this product. But one, now my whole family wants one each. My neighbours are asking me to buy more for them.",
                rating = 20,
                image = ""
            ),
            Review(
                id = "5",
                name = "Rejoice Monalisa Mustapha",
                comment = "Makes you shine",
                rating = 100,
                image = ""
            )

        )
    }
}
