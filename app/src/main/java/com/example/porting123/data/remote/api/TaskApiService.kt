package com.example.porting123.data.remote.api

import com.example.porting123.data.remote.dto.APITaskDto
import retrofit2.http.GET

interface TaskApiService {
    @GET("todos")
    suspend fun getTasks(): List<APITaskDto>
}
