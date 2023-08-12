package com.example.myapp.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment

import com.example.myapp.R
import com.example.myapp.databinding.FragmentHomeBinding
import com.example.myapp.login.LoginActivity.ToastManager.showToast
import com.example.myapp.ui.decibel.SoundLevel
import com.example.myapp.ui.decibel.SoundLevelService
import com.example.myapp.ui.decibel.SoundLevelSetup
import com.example.myapp.ui.decibel.SoundVerified

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val soundType: MutableLiveData<String> = MutableLiveData()
    val currentdB: MutableLiveData<Double> = MutableLiveData()
    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = LocalDateTime.now()              // 현재 시간 가져오기

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val doublePressRunnable = Runnable {
        doubleBackToExitPressedOnce = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (NavHostFragment.findNavController(this@HomeFragment).previousBackStackEntry != null) {
                    NavHostFragment.findNavController(this@HomeFragment).navigateUp()
                } // 이전 프래그먼트가 Null이 아닌경우(존재하는 경우) 뒤로가기
                else {
                    if (doubleBackToExitPressedOnce) {
                        handler.removeCallbacks(doublePressRunnable)
                        isEnabled = false
                        requireActivity().onBackPressed()
                        return
                    }
                    doubleBackToExitPressedOnce = true
                    showToast(requireContext(), "한번 더 누르면 종료합니다")
                    handler.postDelayed(doublePressRunnable, 1500)
                } // 이전 프래그먼트가 존재하지 않는 경우
            }
        }) // 뒤로가기 버튼 동작 함수

        val timeSegment = when (currentTime.hour) {                         // 현재 시각 (시) 만 가져오기
            in 2 until 6 -> "새벽"             // 시간대 분류
            in 6 until 10 -> "아침"
            in 10 until 14 -> "낮"
            in 14 until 18 -> "오후"
            in 18 until 22 -> "저녁"
            else -> "밤"                         // 22시 ~ 02시
        }

        requestSound(1) { soundLevel ->
            if (soundLevel != null) {
                val soundLevelData = soundLevel?.results?.firstOrNull()             // SoundLevel의 최신 데이터 가져오기
                if ( currentTime.hour < 6 || 22 < currentTime.hour) {
                    currentdB.value = soundLevelData?.value ?: 34.0
                } else {
                    currentdB.value = soundLevelData?.value ?: 39.0
                }
            }
        }

        requestSoundVerf(1) { soundVerified ->
            if (soundVerified != null) {
                val soundDataVerf = soundVerified?.results?.firstOrNull()         // SoundLevelVerified의 최신 데이터 가져오기
                soundType.value = soundDataVerf?.sound_type.toString()                // 최신 데이터의 sound_type 가져오기
            }
        }
        val soundIndex = calculateSoundIndex(timeSegment, soundType.value.toString())    // 소음 지수 계산 함수
        displaySoundIndex(timeSegment, soundIndex)                      // 소음 지수 계산 및 표시
        Log.d("소음 데이터 및 소음지수", "${currentdB.value}, ${soundType.value}, ${timeSegment}, ${soundIndex}")
    }   // onViewCreated

    fun requestSound(page: Int, callback: (SoundLevel?) -> Unit) {
        val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        if (token != null) {
            val soundLevelService: SoundLevelService = SoundLevelSetup.service
            val call = soundLevelService.getSoundLevelHome("Token $token", null, page)
            call.enqueue(object : Callback<SoundLevel> {
                override fun onResponse(call: Call<SoundLevel>, response: Response<SoundLevel>) {
                    if(response.isSuccessful) {
                        callback(response.body())
                    }
                }
                override fun onFailure(call: Call<SoundLevel>, t: Throwable) {
                    // callback(null)  // 에러 처리
                }
            })
        }
    }

    fun requestSoundVerf(page: Int, callback: (SoundVerified?) -> Unit) {
        val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token != null) {
            val soundLevelService: SoundLevelService = SoundLevelSetup.service
            val call = soundLevelService.getSoundLevelVerified("Token $token", page)

            call.enqueue(object : Callback<SoundVerified> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<SoundVerified>, response: Response<SoundVerified>) {
                    if(response.isSuccessful) {
                        callback(response.body())
                    }
                }
                override fun onFailure(call: Call<SoundVerified>, t: Throwable) {
                    // callback(null)  // 에러 처리
                }
            })
        } else {
            // callback(null)       // 토큰이 없는 경우 처리
        }
    }


    private fun calculateSoundIndex(timeSegment: String, soundType: String): Double {     // 소음 지수 계산 함수
        when (timeSegment) {                                                              // 시간대와 soundType에 따라 소음 지수를 계산
            "새벽" -> return when(soundType) {
                "발걸음소리" -> 100.0 * (currentdB.value?.div(34.0) ?: 1.0)                                                      // 소음지수 계산식
                "가구끄는소리" -> 24.0 * (currentdB.value?.div(34.0) ?: 1.0)
                "악기소리" -> 8.7 * (currentdB.value?.div(34.0) ?: 1.0)
                else -> 7.3 * (currentdB.value?.div(34.0) ?: 1.0)
            }
            "아침" -> return when(soundType) {
                "발걸음소리" -> 33.3 * (currentdB.value?.div(39.0) ?: 1.0)                                                     // 소음지수 계산식
                "가구끄는소리" -> 36.0  * (currentdB.value?.div(39.0) ?: 1.0)
                "악기소리" -> 9.7  * (currentdB.value?.div(39.0) ?: 1.0)
                else -> 7.3  * (currentdB.value?.div(39.0) ?: 1.0)
            }
            "낮" -> return when(soundType) {
                "발걸음소리" -> 16.7 * (currentdB.value?.div(39.0) ?: 1.0)                                                      // 소음지수 계산식
                "가구끄는소리" -> 24.0  * (currentdB.value?.div(39.0) ?: 1.0)
                "악기소리" -> 8.7  * (currentdB.value?.div(39.0) ?: 1.0)
                else -> 7.3  * (currentdB.value?.div(39.0) ?: 1.0)
            }
            "오후" -> return when(soundType) {
                "발걸음소리" -> 16.7 * (currentdB.value?.div(39.0) ?: 1.0)                                                      // 소음지수 계산식
                "가구끄는소리" -> 36.0 * (currentdB.value?.div(39.0) ?: 1.0)
                "악기소리" -> 17.3 * (currentdB.value?.div(39.0) ?: 1.0)
                else -> 7.3 * (currentdB.value?.div(39.0) ?: 1.0)
            }
            "저녁" -> return when(soundType) {
                "발걸음소리" -> 66.7 * (currentdB.value?.div(39.0) ?: 1.0)                                                   // 소음지수 계산식
                "가구끄는소리" -> 12.0 * (currentdB.value?.div(39.0) ?: 1.0)
                "악기소리" -> 43.3 * (currentdB.value?.div(39.0) ?: 1.0)
                else -> 22.0 * (currentdB.value?.div(39.0) ?: 1.0)
            }
            else -> return when(soundType) {                        // "밤" 시간대
                "발걸음소리" -> 180.0 * (currentdB.value?.div(34.0) ?: 1.0)                                                       // 소음지수 계산식
                "가구끄는소리" -> 84.0 * (currentdB.value?.div(34.0) ?: 1.0)
                "악기소리" -> 26.0 * (currentdB.value?.div(34.0) ?: 1.0)
                else -> 36.7 * (currentdB.value?.div(34.0) ?: 1.0)
            }
        }
    }

    private fun displaySoundIndex(timeSegment: String, soundIndex: Double) {         // 소음 지수 표시 함수
        val (statusText, imageResId) = when (soundIndex.toInt()) {                  // 소음 지수에 따라 텍스트와 이미지 변경
            in 0 until 30 -> Pair("좋음", R.drawable.face_good)
            in 30 until 60 -> Pair("평범", R.drawable.face_normal)
            in 60 until 90 -> Pair("주의", R.drawable.face_alert)
            in 90 until 120 -> Pair("경고", R.drawable.face_warn)
            in 120 until 150 -> Pair("위험", R.drawable.face_danger)
            else -> Pair("심각", R.drawable.face_serious)
        }

        binding.txtNoistStatus.text = statusText
        binding.imageFace.setImageResource(imageResId)
        binding.txtNoiseIndex.text = "${soundIndex.toInt()}"
        binding.txtTimeSegment.text = timeSegment
        binding.txtSoundType.text = "${soundType.value}"
        binding.txtWanring.text = when(timeSegment) {
            "새벽" -> "발걸음 소리에 민감한 시간대입니다.\n살살 걸어주세요"
            "아침" -> "비정상적인 소음에만 주의해주세요"
            "낮" -> "비정상적인 소음에만 주의해주세요"
            "오후" -> "가구 끄는 소리에 주의해 주세요"
            "저녁" -> "하루의 마무리를 준비하는 시간대입니다.\n걸음소리, 악기소리에 주의해 주세요"
            else -> "자그마한 소리에도 굉장히 민감한 시간대입니다.\n소음 발생에 주의해 주세요"          // 밤
        }

        /*
        if (soundIndex >= 90) {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/noiseAlert")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed to noise alert topic"
                    if (!task.isSuccessful) {
                        msg = "Subscription to noise alert topic failed"
                    }
                    Log.d(TAG, msg)
                }
            val data: MutableMap<String, String> = HashMap()
            data["title"] = "Noise Alert"
            if (soundIndex >= 150) {
                data["body"] = "소음 지수가 150 이상입니다. 주의하세요!"
            } else if (soundIndex >= 120) {
                data["body"] = "소음 지수가 120 이상입니다. 주의하세요!"
            } else {
                data["body"] = "소음 지수가 90 이상입니다. 주의하세요!"
            }
            val notification = RemoteMessage.Notification(
                "/topics/noiseAlert",
                data
            )

            sendNotification(notification)
        }
        */
    }


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(doublePressRunnable) // 뒤로가기 버튼 동작함수 메모리에서 제거
        _binding = null
    }
}

