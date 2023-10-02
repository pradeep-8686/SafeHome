package com.example.safehome.model

data class Blocks(
    val `data`: List<BlocksData>,
    val message: String,
    val statusCode: Int
)