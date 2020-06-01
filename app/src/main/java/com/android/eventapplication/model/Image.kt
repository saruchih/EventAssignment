package com.android.eventapplication.model

data class Image(
    val caption: Any,
    val height: String,
    val medium: Medium,
    val small: Small,
    val thumb: Thumb,
    val url: String,
    val width: String
)