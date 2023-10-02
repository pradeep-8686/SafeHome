package com.example.safehome.model

data class TotalDuesMaintenanceList(
    val `data`: List<MyDuesMaintenanceDetails>,
    val message: String,
    val statusCode: Int
)
