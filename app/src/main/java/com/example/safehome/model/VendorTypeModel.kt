package com.example.safehome.model

import java.io.Serializable

data class VendorTypeModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) : Serializable{
    data class Data(
        val isActive: Boolean,
        val name: String,
        val vendorTypeId: Int
    ) :Serializable
}