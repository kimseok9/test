package com.example.myapp.ui.decibel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapp.R
import com.example.myapp.databinding.FragmentRecordDetailBinding
import kotlinx.coroutines.NonCancellable.start

class RecordDetailFragment : Fragment() {

    private var _binding : FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }   // onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val soundFileUrl = arguments?.getString("sound_file")
        soundFileUrl?.let {
            prepareMediaPlayer(it)
        }

        val progressBar = binding.progressBar
        val playPauseButton = binding.playPauseButton

        playPauseButton.setOnClickListener {
            if (mediaPlayer == null) {
                // MediaPlayer가 아직 초기화되지 않았다면, 여기에서 초기화합니다.
                progressBar.visibility = View.VISIBLE  // ProgressBar를 보이게 합니다.
                val audioUrl = arguments?.getString("sound_file")  // 이전 프래그먼트에서 전달받은 URL
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl)  // 오디오 스트림의 위치를 설정합니다.
                    setOnPreparedListener {
                        progressBar.visibility = View.INVISIBLE  // 준비가 끝나면 ProgressBar를 숨깁니다.
                        start()  // 오디오를 재생합니다.
                    }
                    setOnCompletionListener {
                        // 오디오 재생이 완료되면 MediaPlayer를 해제하고, 버튼의 텍스트를 변경합니다.
                        release()
                        mediaPlayer = null
                        playPauseButton.text = "재생"
                    }
                    prepareAsync()  // MediaPlayer를 비동기적으로 준비합니다.
                }
                playPauseButton.text = "일시정지"
            } else if (mediaPlayer!!.isPlaying) {
                // 오디오가 재생 중이라면 일시정지합니다.
                mediaPlayer?.pause()
                playPauseButton.text = "다시 재생"
            } else {
                // 오디오가 일시정지 상태라면 재생을 계속합니다.
                mediaPlayer?.start()
                playPauseButton.text = "일시정지"
            }
        }
    } // onViewCreated

    private fun prepareMediaPlayer(soundFileUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(soundFileUrl)
            setOnPreparedListener {
                start()
            }
            prepareAsync()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null

        _binding = null
    }


}