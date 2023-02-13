package com.deadrudolph.home_domain.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UserResponse(

    @Json(name = "id")
    val id: String,

    @Json(name = "fullName")
    val fullName: String,

    @Json(name = "firstName")
    val firstName: String,

    @Json(name = "lastName")
    val lastName: String,

    @Json(name = "gender")
    val gender: String,

    @Json(name = "birthday")
    val birthday: String,

    @Json(name = "age")
    val age: Int,

    @Json(name = "avatar")
    val avatar: String,

    @Json(name = "address")
    val address: String,

    @Json(name = "zip")
    val zip: String,

    @Json(name = "state")
    val state: String,

    @Json(name = "phone")
    val phone: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "twitter")
    val twitter: String,

    @Json(name = "ssn")
    val ssn: String,
)