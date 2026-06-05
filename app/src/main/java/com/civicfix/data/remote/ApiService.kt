package com.civicfix.data.remote

import com.civicfix.data.remote.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): AuthResponse

    // Issues
    @GET("issues")
    suspend fun getIssues(
        @Query("category") category: String? = null,
        @Query("status")   status: String? = null,
        @Query("search")   search: String? = null
    ): List<IssueDto>

    @GET("issues/my")
    suspend fun getMyIssues(): List<IssueDto>

    @GET("issues/{id}")
    suspend fun getIssue(@Path("id") id: Int): IssueDto

    @GET("issues/{id}/timeline")
    suspend fun getTimeline(@Path("id") id: Int): List<TimelineDto>

    @Multipart
    @POST("issues")
    suspend fun createIssue(
        @Part("title")       title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category")    category: RequestBody,
        @Part("latitude")    latitude: RequestBody,
        @Part("longitude")   longitude: RequestBody,
        @Part("address")     address: RequestBody,
        @Part image: MultipartBody.Part?
    ): IssueDto

    @POST("issues/{id}/vote")
    suspend fun voteIssue(@Path("id") id: Int): VoteResponse

    @POST("issues/{id}/comments")
    suspend fun addComment(@Path("id") id: Int, @Body req: CommentRequest): CommentDto
}
