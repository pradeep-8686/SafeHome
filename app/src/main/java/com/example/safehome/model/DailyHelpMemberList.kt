package com.example.safehome.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DailyHelpMemberList(
    @SerializedName("Name") var Name: String = "",
    @SerializedName("availablityTime") var availablityTime: String = "",
    @SerializedName("availablityOn") var availablityOn: String = "",
    @SerializedName("works_in") var works_in: String = "",
    @SerializedName("mobilenumber")  var mobilenumber: String = "",
    @SerializedName("rating")  var rating: String = "",
    @SerializedName("start_date") var start_date: String = "",
    @SerializedName("end_date") var end_date: String = "",
    @SerializedName("start_time") var start_time: String = "",
    @SerializedName("end_time") var end_time: String = "",
    @SerializedName("role") var role: String = ""
) : Serializable
