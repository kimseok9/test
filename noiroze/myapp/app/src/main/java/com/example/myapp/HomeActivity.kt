package com.example.myapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapp.databinding.ActivityHomeBinding

import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var mbinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mbinding.root)

        val navView: BottomNavigationView = mbinding.myBottomNav  // 바텀 네비게이션 메뉴 포함
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host) as NavHostFragment   // 네비게이션들을 담는 호스트 -> 프래그먼트 전환시 나타날 화면을 담는 공간
        val navController = navHostFragment.navController    // 네비게이션 컨트롤러 -> 실제 내비게이션 메뉴를 연결

        navView.setupWithNavController(navController)   // 네비게이션 뷰와 컨트롤러와 연결 -> BottomNavigationView와 NavController를 연결하여 BottomNavigationView의 네비게이션 기능이 설정
    } // onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {              // 메뉴를 생성하고 메뉴 아이템의 클릭 이벤트 처리
        menuInflater.inflate(R.menu.top_app_bar, menu)  // top_app_bar.xml을 menu 객체로 인플레이트.  xml에 정의된 아이템들이 menu 객체에 추가돼
        return super.onCreateOptionsMenu(menu)    // 나머지 기본 동작 처리
    }
}