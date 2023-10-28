package com.example.safehome.model

data class PollsPostedDropDownModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val isActive: Boolean,
        val name: String,
        val postedToId: Int
    )
}