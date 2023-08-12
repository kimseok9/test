package com.example.myapp.login

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.myapp.HomeActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {
    val loginState = MutableLiveData<LoginState>()

    sealed class LoginState {
        data class Success(val user: LoginUser) : LoginState()
        data class Failure(val message: String) : LoginState()
    } // Login 상태를 나타내는 sealed class

    fun loginUser(userid: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) { // 코루틴에서 I/O 에 최적화된 쓰레드를 불러와서 작업함
            try {
                val loginResponse = Login.service.userLogin(userid, password)               // service의 로그인함수 변수 정의, Suspended함수
                withContext(Dispatchers.Main) {                                             // UI 작업 등은 메인 스레드에서 실행되어야 하므로, Dispatchers.Main 을 통해, 메인 스레드를 다시 불러옴
                    if (loginResponse.isSuccessful && loginResponse.body() != null) {       // 로그인 함수처리 성공 && 로그인 데이터가 null이 아닌 경우
                        loginState.value = LoginState.Success(loginResponse.body()!!)
                    }   // 로그인 성공 시
                    else {
                        loginState.value = LoginState.Failure("아이디와 비밀번호를 확인해 주세요.")    // 로그인 실패 시, 메세지 할당
                    }   // 로그인 실패 시
                }
            }   // 로그인 시도
            catch(e: SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    loginState.value = LoginState.Failure("서버 응답이 너무 오래 걸립니다. 잠시 후 다시 시도해주세요.")
                }
            }   // 로그인 응답 타임아웃 에러 발생 시
            catch(e: Exception) {
                withContext(Dispatchers.Main) {
                    loginState.value = LoginState.Failure("알수없는 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.")
                }
            }   // 이외의 오류 발생 시
        }
    }  // loginUser 함수

} // LoginViewModel