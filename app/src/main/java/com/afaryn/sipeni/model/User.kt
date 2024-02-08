package com.afaryn.sipeni.model

data class User(
    val name: String? = null,
    val email: String? = null,
    val jenisKelamin: String? = null,
    val phone: String? = null,
    val profile: String? = null,
    val nik: String? = null,
    val role: String? = "user"
)