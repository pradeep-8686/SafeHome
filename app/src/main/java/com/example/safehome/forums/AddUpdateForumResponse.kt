package com.example.safehome.forums

data class AddUpdateForumResponse(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val forumId: Int
    )
}