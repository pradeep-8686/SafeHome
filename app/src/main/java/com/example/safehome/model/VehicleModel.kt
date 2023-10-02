package com.example.safehome.model

data class VehicleModel(
    val `data`: List<VehicleModelResp>,
    val message: String,
    val statusCode: Int
)

data class VehicleModelResp(
    val vehicleModelId : Int = 0,
    val vehicleModel : String = "",
    val isActive : String = "",
    val vehicleType : Int = 4
)