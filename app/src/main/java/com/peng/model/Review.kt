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
            ),
            Review(
                id = "6",
                name = "Mark Sam",
                comment = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                rating = 85,
                image = ""
            ),
            Review(
                id = "7",
                name = "Priscilla Donald Mike",
                comment = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                rating = 54,
                image = ""
            ),
            Review(
                id = "8",
                name = "Achimugu Osapa",
                comment = "Makes you shine brighter than the sun",
                rating = 77,
                image = ""
            ),
            Review(
                id = "9",
                name = "Cynthia Berry",
                comment = "I love this product. But one, now my whole family wants one each. My neighbours are asking me to buy more for them.",
                rating = 20,
                image = ""
            ),
            Review(
                id = "10",
                name = "Rejoice Monalisa Mustapha",
                comment = "Makes you shine",
                rating = 100,
                image = ""
            ),
            Review(
                id = "11",
                name = "Mark Sam",
                comment = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                rating = 85,
                image = ""
            ),
            Review(
                id = "12",
                name = "Priscilla Donald Mike",
                comment = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                rating = 54,
                image = ""
            ),
            Review(
                id = "13",
                name = "Achimugu Osapa",
                comment = "Makes you shine brighter than the sun",
                rating = 77,
                image = ""
            ),
            Review(
                id = "14",
                name = "Cynthia Berry",
                comment = "I love this product. But one, now my whole family wants one each. My neighbours are asking me to buy more for them.",
                rating = 20,
                image = ""
            ),
            Review(
                id = "15",
                name = "Rejoice Monalisa Mustapha",
                comment = "Makes you shine",
                rating = 100,
                image = ""
            ),
            Review(
                id = "16",
                name = "Mark Sam",
                comment = "Makes you shine brighter than the sun. " +
                        "All you need to spice up your beaut. " +
                        "Minerals and essential vitamins present in rich amounts.",
                rating = 85,
                image = ""
            ),
            Review(
                id = "17",
                name = "Priscilla Donald Mike",
                comment = "Yes, makes you shine brighter than the sun, Makes you shine brighter than the sun",
                rating = 54,
                image = ""
            ),
            Review(
                id = "18",
                name = "Achimugu Osapa",
                comment = "Makes you shine brighter than the sun",
                rating = 77,
                image = ""
            ),
            Review(
                id = "19",
                name = "Cynthia Berry",
                comment = "I love this product. But one, now my whole family wants one each. My neighbours are asking me to buy more for them.",
                rating = 20,
                image = ""
            ),
            Review(
                id = "20",
                name = "Rejoice Monalisa Mustapha",
                comment = "Makes you shine",
                rating = 100,
                image = ""
            )

        )
    }
}
