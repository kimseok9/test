package com.example.myapp.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider

import com.example.myapp.HomeActivity
import com.example.myapp.R
import com.example.myapp.databinding.ActivityLoginBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    companion object ToastManager {
        private var currentToast: Toast? = null     // 토스트메세지 초기화
        fun showToast(context: Context?, message: String) {
            currentToast?.cancel()
            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()
        }
    } // 토스트 메세지 관리하는 오브젝트

    private lateinit var mbinding : ActivityLoginBinding      // 바인딩 변수선언. lateinit으로 나중에 변수초기화 필요.
    private lateinit var loginViewModel : LoginViewModel    // 뷰모델 사용. 변수 초기화

    private lateinit var title : ImageView
    private lateinit var userIdInput : EditText
    private lateinit var pwdInput : EditText

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val doublePressRunnable = Runnable {
        doubleBackToExitPressedOnce = false
    } // 뒤로가기 버튼 관련 변수들

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        title = mbinding.mainLogo
        userIdInput = mbinding.IDinput
        pwdInput = mbinding.PWDinput
        val loginBtn = mbinding.loginBtn
        val transitionDrawable = ResourcesCompat.getDrawable(resources,
            R.drawable.button_transition,null) as TransitionDrawable        // 색상변경 변수 설정
        loginBtn.background = transitionDrawable                                   // 로그인버튼 배경에 색상변경 적용


        loginViewModel.loginState.observe(this) { loginState ->
            when(loginState) {
                is LoginViewModel.LoginState.Success -> {   // LoginState가 Success인 경우(로그인 성공인 경우) 처리하는 부분
                    val user = loginState.user
                    val sharedPref = getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("user_id", user.userid)
                        putString("token", user.token)
                        putString("user_dong", user.dong)
                        putString("user_ho", user.ho)
                        apply()
                    }   // sharedPref, editor를 사용하여 받아온 데이터를 저장하고, 다른 액티비티나 프래그먼트로 공유
                    showToast(this@LoginActivity, "로그인 성공!")
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }   // LoginState가 Success인 경우(로그인 성공인 경우) 처리하는 부분
                is LoginViewModel.LoginState.Failure -> {   // LoginState가 Failure인 경우(로그인 실패인 경우) 처리하는 부분
                    AlertDialog.Builder(this)
                        .setTitle("로그인 실패")
                        .setMessage(loginState.message)
                        .setPositiveButton("확인", null)
                        .show()
                }   // LoginState가 Failure인 경우(로그인 실패인 경우) 처리하는 부분
            }  // LoginState를 확인하고, 경우에 따라 이를 처리하는 부분
        } // loginState 관찰 함수


        loginBtn.setOnClickListener {
            transitionDrawable.startTransition(100) // 0.2초 동안 색상 변경
            loginBtn.postDelayed({
                transitionDrawable.reverseTransition(100) // 0.2초 동안 색상 복원
            }, 100L)

            val userid = userIdInput.text.toString()
            val password = pwdInput.text.toString()     // 아이디, 비밀번호 란에 입력한 정보를 가져옴.

            if (userid.isBlank() || password.isBlank()) {
                showToast(this, "아이디와 비밀번호를 입력해주세요.")
            } // 로그인 입력칸이 둘중 하나라도 비어있는 경우
            else {
                loginViewModel.loginUser(userid, password)
            } // 로그인 입력란에 정보가 입력이 된 경우
        } // loginBtn 클릭시 실행되는 함수

    } // onCreate

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        } // 버튼을 1.5초 이내에 두 번 누르면 종료 (doubleBackToExitPressedOnce = true)
        else {
            this.doubleBackToExitPressedOnce = true
            showToast(this, "한번 더 누르면 종료합니다.")
            handler.postDelayed(doublePressRunnable, 1500)
        } // 버튼을 한 번 누르면 상태를 true로 바꾸고, 토스트메세지 표시
    } // onBackPressed 뒤로가기 버튼 함수


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(doublePressRunnable)
    } // onDestroy
}



/*
private fun loginUser(userid: String, password: String) {
        launch(Dispatchers.IO) {                                                // 코루틴에서 I/O 에 최적화된 쓰레드를 불러와서 작업함
            val loginResponse = service.userLogin(userid, password)             // service의 로그인함수 변수 정의, Suspended함수
            withContext(Dispatchers.Main) {                                     // UI 작업 등은 메인 스레드에서 실행되어야 하므로, Dispatchers.Main 을 통해, 메인 스레드를 다시 불러옴
                if (loginResponse.isSuccessful && loginResponse.body() != null) {            // 로그인 함수처리 성공 && 로그인 데이터가 null이 아닌 경우
                    val sharedPref = getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putString("user_id", userid)
                    editor.putString("token", loginResponse.body()?.token)
                    editor.putString("user_dong", loginResponse.body()?.dong)
                    editor.putString("user_ho", loginResponse.body()?.ho)
                    editor.apply()                                              // sharedPref, editor를 사용하여 받아온 데이터를 저장하고, 다른 액티비티나 프래그먼트로 공유
                    // val dong = loginResponse.body()?.dong
                    // val ho = loginResponse.body()?.ho
                    // Log.d("로그인 성공 정보", "User dong: ${dong}, User ho: ${ho}")     // 유저 정보를 잘 받아왔는지 확인하는 로그
                    showToast(this@LoginActivity, "로그인 성공!")
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {                                                                   // 로그인 함수처리 실패 || 로그인 데이터가 null인 경우
                    AlertDialog.Builder(this@LoginActivity)                      // 로그인 실패 시 대화 상자를 표시
                        .setTitle("로그인 실패")
                        .setMessage("아이디 또는 비밀번호를 확인해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }
    }  // loginUser
*/
