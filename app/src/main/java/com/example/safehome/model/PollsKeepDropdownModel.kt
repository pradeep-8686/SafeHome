package com.example.safehome.model

data class PollsKeepDropdownModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val isActive: Boolean,
        val keepFor: String,
        val keepId: Int
    )
}