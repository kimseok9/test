package com.example.myapp.ui.community.service

import com.example.myapp.login.Login.client
import com.example.myapp.login.URL
import com.google.gson.annotations.SerializedName

import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommunityBoardSetup {                 // Retrofit 사용을 위한 객체 생성 및 설정.
    private val retrofit = Retrofit.Builder()    // retrofit 인스턴스 생성
        .baseUrl(URL)  //  API 서버가 실행 중인 컴퓨터의 IP 주소를 기본 url로 지정
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())    // json을 java 객체로 변환하기 위한 Gson
        .build()

    val service get() = retrofit.create(CommunityBoardService::class.java)  // CommunityBoardService 인터페이스를 구현한 서비스 객체를 생성하는 프로퍼티
}

interface CommunityBoardService {     // RESTful API 요청을 정의하는 인터페이스
    @GET("/api/community_board/")        // retrofit의 get 이노테이션 사용- GET 요청
    fun requestCommunityBoardList(@Query("page") page: Int): Call<CommunityBoards>  // page라는 쿼리 매개변수를 받아서 게시판 목록을 가져오는 API 요청을 정의

    @POST("/api/community_board/") // 실제 API 엔드포인트에 맞게 변경해야 합니다.
    fun createPost(@Body request: CreatePostRequest): Call<ResponseBody>
}

data class CommunityBoards(         // http://noiroze.com/api/community_board 의 JSON 데이터 형식
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")   // http://noiroze.com/api/community_board
    val previous: String,
    @SerializedName("results")   // 커뮤니티 게시판 리스트
    val results: List<Result>
)

data class Result(   // 커뮤니티 게시판 상세내용
    @SerializedName("category")
    val category: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("created_date")
    val created_date: String,
    @SerializedName("modify_date")
    val modify_date: String,
    @SerializedName("like")
    val like: List<Int>,
)
{
    val createdDate: Date
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            return format.parse(created_date) ?: Date()
        }
}             // 작성일시, 수정일시를 Date 포맷으로 변경


fun getTimeDifference(date: Date): String {
    val now = Date()
    val diffInMinutes = (now.time - date.time) / 60000

    return when {
        diffInMinutes < 60 -> "$diffInMinutes 분 전"
        diffInMinutes < 1440 -> "${diffInMinutes / 60} 시간 전"
        else -> "${diffInMinutes / 1440} 일 전"
    }
}             // 작성일시, 수정일시를 현재 시간과의 차이로 표시하기 위한 함수


data class CreatePostRequest(
    val category: String,
    val title: String,
    val content: String,
    val author: String,
    val created_date: String,
)


