package com.example.myapp.ui.decibel


import com.example.myapp.login.Login.client
import com.example.myapp.login.URL
import com.google.gson.annotations.SerializedName

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

object SoundLevelSetup {
    private val retrofit = Retrofit.Builder()    // retrofit 인스턴스 생성
        .baseUrl(URL)  //  API 서버가 실행 중인 컴퓨터의 IP 주소를 기본 url로 지정
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())    // json을 java 객체로 변환하기 위한 Gson
        .build()

    val service get() = retrofit.create(SoundLevelService::class.java)
}

interface SoundLevelService {
    @GET("/api/sound_level/")        // retrofit의 get 이노테이션 사용- GET 요청
    fun getSoundLevelHome(
        @Header("Authorization") token: String,
        @Query("date") date: String?,
        @Query("page") page: Int
    ) : Call<SoundLevel>

    @GET("/api/sound_level/")        // retrofit의 get 이노테이션 사용- GET 요청
    fun getSoundLevelAll(@Query("page") page: Int) : Call<SoundLevel>

    @GET("/api/sound_level/")
    fun getSoundLevelDong(
        @Query("dong") dong: String,
        @Query("date") date: String?,
        @Query("page") page: Int
    ) : Call<SoundLevel>

    @GET("/api/sound_verified/")        // retrofit의 get 이노테이션 사용- GET 요청
    fun getSoundLevelVerified(@Header("Authorization") token: String, @Query("page") page: Int) : Call<SoundVerified>

    @GET("/api/sound_file/")
    fun getSoundRecordFile(@Header("Authorization") token: String, @Query("page") page: Int) : Call<SoundFile>
}

data class SoundLevel (
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String?,
    @SerializedName("previous")   // http://서버 주소/api/community_board
    val previous: String?,
    @SerializedName("results")   // 커뮤니티 게시판 리스트
    val results: List<SLResult>
)

data class SLResult (
    @SerializedName("dong")
    val dong: String,
    @SerializedName("ho")
    val ho: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("value")
    val value: Double,
    @SerializedName("created_at")
    val created_at: String,
)


data class SoundVerified (
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")   // http://서버 주소/api/community_board
    val previous: String,
    @SerializedName("results")   // 커뮤니티 게시판 리스트
    val results: List<SVResult>
)


data class SVResult (
    @SerializedName("dong")
    val dong: String,
    @SerializedName("ho")
    val ho: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("value")
    val value: Double,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("sound_type")
    val sound_type: String,
)

data class SoundFile (
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")   // http://서버 주소/api/community_board
    val previous: String,
    @SerializedName("results")   // 커뮤니티 게시판 리스트
    val results: List<SFResult>
)

data class SFResult (
    @SerializedName("dong")
    val dong: String,
    @SerializedName("ho")
    val ho: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("value")
    val value: Double,
    @SerializedName("file_name")
    val file_name: String,
    @SerializedName("sound_file")
    val sound_file: String,
    @SerializedName("created_at")
    val created_at: String,
)