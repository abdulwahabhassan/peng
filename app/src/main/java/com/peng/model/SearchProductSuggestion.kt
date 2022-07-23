package com.peng.model

data class SearchProductSuggestion(
    val text: String
) {
    companion object {
        val results = listOf(
            SearchProductSuggestion("Iphone x"),
            SearchProductSuggestion("Cranberry juice"),
            SearchProductSuggestion("Bottle neck clip"),
            SearchProductSuggestion("Facial"),
            SearchProductSuggestion("Samsung plasma TV"),
            SearchProductSuggestion("Body"),
            SearchProductSuggestion("Toiletries")

        )
    }
}


