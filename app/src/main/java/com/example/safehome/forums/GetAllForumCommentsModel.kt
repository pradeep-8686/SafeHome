package com.example.safehome.forums

data class GetAllForumCommentsModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val comment: String,
        val commentCreatedBy: Int,
        val commentCreatedByName: String,
        val commentId: Int,
        val commentReplies: List<CommentReply>,
        val forumId: Int
    ) {
        data class CommentReply(
            val commentId: Int,
            val forumCommentReplyId: Int,
            val message: String,
            val replyCreatedBy: Int,
            val replyCreatedByName: String
        )
    }
}