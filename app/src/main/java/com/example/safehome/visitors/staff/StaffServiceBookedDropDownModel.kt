package com.example.safehome.visitors.staff

data class StaffServiceBookedDropDownModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val mobileNo: String,
        val staffId: Int,
        val staffName: String,
        var isSelected: Boolean = false

    )
}