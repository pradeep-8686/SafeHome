package com.example.safehome.model

data class TenantDetails(
    val `data`: List<Tenant>,
    val message: String,
    val statusCode: Int
)