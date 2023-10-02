package com.example.safehome.model

data class Vehicle(
    val createdBy: Int=1,
    val createdOn: String="",
    val modifiedBy: Any="",
    val modifiedOn: Any="",
    val parkingLotNumber: String="",
    val status: Boolean=false,
    val stickerIssued: String="",
    val userId: Int=1,
    val vehicleId: Int=1,
    val vehicleModel: String="",
    val vehicleNumber: String="",
    val vehicleType: String="",
    val type: String = "HadData"
)