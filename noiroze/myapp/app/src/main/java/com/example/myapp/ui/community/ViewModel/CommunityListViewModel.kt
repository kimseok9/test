package com.example.myapp.ui.community.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.myapp.ui.community.service.CommunityBoardSetup
import com.example.myapp.ui.community.service.CommunityBoards
import com.example.myapp.ui.community.service.Result

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityListViewModel : ViewModel() {
    private val _boardList = MutableLiveData<List<Result>>()
    val boardList: LiveData<List<Result>> get() = _boardList

    private val _filteredBoardList = MutableLiveData<List<Result>>()
    val filteredBoardList: LiveData<List<Result>> get() = _filteredBoardList

    private var currentPage = 1
    private var hasNextPage = true
    private var currentCategory = "전체보기"  // 주제별 목록 필터링. 기본값 "전체보기"

    fun loadBoardList() {
        if (!hasNextPage) return     // 다음 페이지가 없는 경우, 리턴값 X

        val communityBoardList = CommunityBoardSetup.service.requestCommunityBoardList(page = currentPage)
        communityBoardList.enqueue(object : Callback<CommunityBoards> {
            override fun onResponse(call: Call<CommunityBoards>, response: Response<CommunityBoards>) {
                if (response.isSuccessful) {                                                        // 응답이 성공하면
                    val newBoardList = response.body()                                              // newBoardList 변수에 응답내용을 할당
                    hasNextPage = !newBoardList?.next.isNullOrEmpty()                               // 변수의 next 값이 있는지 확인하고, hasNextPage가 true인지 false인지 확인
                    val updatedBoardList = _boardList.value.orEmpty() + newBoardList!!.results      // 현재 로드된 게시글 목록(boardList)에 새로 받아온 게시글 목록(newBoardList.results)을 추가
                    _boardList.value = updatedBoardList                                             // boardList.value를 위에서 변경한 값으로 바꿈
                    currentPage++                                                                   // 페이지 1 추가 (다음 페이지의 내용 불러오기)
                    changeCategory(currentCategory)                                                 // 카테고리에 맞게 필터링된 목록을 업데이트
                }
            } // onResponse

            override fun onFailure(call: Call<CommunityBoards>, t: Throwable) {
                Log.e("네트워크 요청 실패", t.toString())
            } // onFailure
        }) // BoardList 데이터 불러오는 부분
    } // loadBoardList

    // 카테고리 변경을 위한 함수
    fun changeCategory(category: String) {
        currentCategory = category
        _filteredBoardList.value = _boardList.value.orEmpty().filter {
            it.category == currentCategory || currentCategory == "전체보기"
        }
    } // changeCategory 카테고리 변경 함수
}