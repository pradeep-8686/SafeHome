package com.example.safehome.model

data class RelationshipTypesModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val isActive: Boolean,
        val relationShipId: Int,
        val relationShipName: String
    )
}