package com.example.client.data.repositories

import com.example.client.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "http://mcc-fall-2019-g25.appspot.com/"

private val moshi = Moshi.Builder()
    .add(ProjectJsonAdapter())
    .add(ProjectCreationJsonAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RepositoryService {

    @GET("projects/{kind}")
    fun getProjects(@Header("Authorization") authorization: String, @Path("kind") kind: String): Call<APIResponse<List<Project>>>

    @GET("project/{projectId}")
    fun getProject(@Path("projectId") id: String, @Header("Authorization") authorization: String): Call<APIResponse<Project>>

    @DELETE("project/{projectId}")
    fun deleteProject(@Path("projectId") id: String, @Header("Authorization") authorization: String): Call<APIResponse<String>>

    @PUT("project/{projectId}/isFavorite")
    fun updateIsFavoriteProject(@Body body: IsFavorite, @Path("projectId") id: String, @Header("Authorization") authorization: String): Call<APIResponse<String>>

    @POST("project")
    fun createProject(@Body project: ProjectCreation, @Header("Authorization") authorization: String): Call<APIResponse<Project>>

    @GET("projects/{by}/search")
    fun searchProject(@Path("by") by: String, @Query("q") q: String, @Header("Authorization") authorization: String): Call<APIResponse<List<Project>>>
}

object ProjectRepository {

    val retrofitService : RepositoryService by lazy {
        retrofit.create(RepositoryService::class.java)
    }

}

class APIResponse<T> (
    val data: T?,
    val message: String?
)

class IsFavorite(val isFavorite: Boolean)


interface UserSearchRepositoryService {
    @GET("users/{name}")
    fun searchUsers(@Path("name") by: String, @Header("Authorization") authorization: String): Call<APIResponse<List<User>>>
}

object UserSearchRepository {

    val retrofitService : UserSearchRepositoryService by lazy {
        retrofit.create(UserSearchRepositoryService::class.java)
    }

}
