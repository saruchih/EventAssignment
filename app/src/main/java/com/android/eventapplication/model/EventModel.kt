package com.android.eventapplication.model

data class EventModel(
    val events: Events,
    val first_item: Any,
    val last_item: Any,
    val page_count: String,
    val page_items: Any,
    val page_number: String,
    val page_size: String,
    val search_time: String,
    val total_items: Int
)