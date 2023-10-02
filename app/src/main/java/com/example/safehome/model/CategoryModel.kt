package com.example.safehome.model

data class CategoryModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val categoryId: Int,
        val categoryName: String,
        val comments: String,
        val isActive: Boolean,
        val shIndividualUserMaintenances: List<Any>,
        val shMaintenanceMains: List<Any>,
        val shMaintenanceSubCategoryMasters: List<Any>
    )
}