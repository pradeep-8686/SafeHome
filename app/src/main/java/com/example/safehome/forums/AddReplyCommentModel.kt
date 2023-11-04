package com.example.safehome.forums

data class AddReplyCommentModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val forumCommentReplyId: Int
    )
}