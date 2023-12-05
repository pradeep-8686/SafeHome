package com.example.safehome.visitors.guest.selectguest

import android.os.Parcelable
import java.io.Serializable

data class ContactsModel(
    val id: String,
    val name : String,
    val number : String
    , var isSelected: Boolean = false
) : Serializable
