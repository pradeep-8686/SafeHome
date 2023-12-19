package com.example.safehome.model

data class VendorDetailsModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val mobileNumber: String,
        val serviceProvided: String,
        val vendorServiceId: Int,
        val vendorTypeId: Int,
        val vendorTypeName: String
    )
}