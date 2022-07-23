package com.peng.model

data class SearchProductSuggestion(
    val text: String
) {
    companion object {
        val results = listOf(
            SearchProductSuggestion("Iphone x"),
            SearchProductSuggestion("Cranberry juice"),
            SearchProductSuggestion("Bottle neck clip"),
            SearchProductSuggestion("Samsung plasma TV"),
            SearchProductSuggestion("Toiletries")

        )
    }
}


