package com.example.safehome.alert

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AlertResponse(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) : Serializable{
    data class Data(
        val forum: ArrayList<Forum>,
        val totalRecords: Int
    ) : Serializable{
        data class Forum(
            val alertId: Int,
            val block: Any,
            val blockId: Any,
            val comments: String,
            val createdBy: Int,
            val createdByName: String,
            val description: String,
            val emergencyTypeId: Int,
            val emergencyTypeName: String,
            val flat: Any,
            val flatId: Any,
            val flatNo: Any,
            val modifiedBy: Any,
            val modifiedOn: Any,
            val notifyTo: List<NotifyTo>,
            val residentId: Any,
            val roleId: Int,
            val sendDate: String,
            val sendTime: String,
            val sentBy: String
        ): Serializable {
            data class NotifyTo(
                val alertId: Int,
                val name: String,
                val postedToId: Int
            ): Serializable
        }
    }
}