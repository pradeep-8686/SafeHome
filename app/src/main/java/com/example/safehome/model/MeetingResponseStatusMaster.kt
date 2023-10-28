package com.example.safehome.model

import java.io.Serializable

data class MeetingResponseStatusMaster(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
): Serializable {
    data class Data(
        val id: Int,
        val isActive: Boolean,
        val name: String,
        val shPollSubmitResults: List<Any>
    ): Serializable
}