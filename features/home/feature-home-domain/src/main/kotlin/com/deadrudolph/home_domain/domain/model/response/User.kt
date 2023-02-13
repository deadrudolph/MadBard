package com.deadrudolph.home_domain.domain.model.response

data class User(
    val id: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthday: String,
    val age: Int,
    val avatar: String,
    val address: String,
    val zip: String,
    val state: String,
    val phone: String,
    val email: String,
    val twitter: String,
    val ssn: String,
)