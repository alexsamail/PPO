package com.example.cx61.ppo

import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties
data class User(
        var email: String = "",
        var firstName: String? = "",
        var lastName: String? = "",
        var phone: String? = "",
        var rssUrl: String = ""
)
