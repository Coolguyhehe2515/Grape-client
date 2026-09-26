package com.grape.client.about

data class Credit(
    val name: String,
    val role: String,
    val link: String
)

data class Credits(
    val version: String,
    val credits: List<Credit>,
    val notice: String
)
