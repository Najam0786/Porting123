package com.example.porting123.data.remote.dto

import com.example.porting123.data.local.entity.TaskEntity
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class APITaskDto(
    @SerializedName("id") val apiId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("completed") val isCompleted: Boolean
)

fun APITaskDto.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = UUID.randomUUID().toString(), // The API doesn't provide a UUID, so we generate one.
        title = this.title,
        description = "", // The API doesn't provide a description.
        isCompleted = this.isCompleted,
        priority = "MEDIUM", // Default priority
        createdAt = System.currentTimeMillis(),
        dueDate = null
    )
}
