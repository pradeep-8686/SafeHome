package com.example.safehome.enews

data class ENewsListModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val eNews: List<ENew>,
        val totalRecords: Int
    ) {
        data class ENew(
            val comments: Any,
            val createdBy: Int,
            val createdByName: String,
            val createdByRole: String,
            val createdOn: String,
            val eNews: String,
            val eNewsId: Int,
            val eNewsImages: List<Any>,
            val fromDate: Any,
            val keepEnewsFor: String,
            val keepEnewsForId: Int,
            val modifiedBy: Any,
            val modifiedOn: Any,
            val postedDate: String,
            val postedTime: String,
            val postedTo: List<PostedTo>,
            val timeStamp: String,
            val toDate: Any,
            val topIcName: String
        ) {
            data class PostedTo(
                val eNewsId: Int,
                val name: String,
                val postedToId: Int
            )
        }
    }
}