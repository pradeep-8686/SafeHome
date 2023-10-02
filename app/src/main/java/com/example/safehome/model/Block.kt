package com.example.safehome.model

data class Block(
    val block: String,
    val blockId: Int,
    val createdBy: Any,
    val createdOn: String,
    val isActive: Boolean,
    val modifiedBy: Any,
    val modifiedOn: Any,
    val noOfFlats: Int,
    val noOfFloors: Int,
    val shFlats: List<Any>
)