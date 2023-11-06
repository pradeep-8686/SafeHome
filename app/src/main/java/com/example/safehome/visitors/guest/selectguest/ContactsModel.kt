package com.example.safehome.visitors.guest.selectguest

data class ContactsModel(
    val name : String,
    val number : String
    , var isSelected: Boolean = false
)
