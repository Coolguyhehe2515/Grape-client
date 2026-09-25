package com.grape.client.announcement

data class Announcement(
    val enabled: Boolean,
    val title: String,
    val message: String,
    val type: String,
    val version: String,
    val url: String,
    val dismissible: Boolean
)
