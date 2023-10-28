package com.example.safehome.model

data class GetPollResultModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val polldata: List<Polldata>,
        val totalVotes: Int
    ) {
        data class Polldata(
            val optionId: Int,
            val optionName: String,
            val pollId: Int,
            val voteCount: Int,
            val votePercentage: Int
        )
    }
}