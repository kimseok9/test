package com.example.myapp.ui.decibel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController

import com.example.myapp.R
import com.example.myapp.databinding.FragmentDecibelBinding

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class DecibelFragment : Fragment() {
    private var _binding : FragmentDecibelBinding? = null
    private val binding get() = _binding!!

    private lateinit var dongChart: BarChart
    private lateinit var hoChart: LineChart
    private lateinit var btnRecord: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDecibelBinding.inflate(inflater, container, false)

        dongChart = binding.dongChart
        dongChart.setNoDataText("데이터를 불러오는 중...")
        hoChart = binding.hoChart
        hoChart.setNoDataText("데이터를 불러오는 중...")

        btnRecord = binding.btnRecordFile
        return binding.root
    }        // onCreateView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawDongChart()
        drawHoChart()

        val transitionDrawable = ResourcesCompat.getDrawable(resources, R.drawable.button_transition,null) as TransitionDrawable
        btnRecord.background = transitionDrawable

        btnRecord.setOnClickListener {
            transitionDrawable.startTransition(100) // 0.2초 동안 색상 변경
            btnRecord.postDelayed({
                transitionDrawable.reverseTransition(100) // 0.2초 동안 색상 복원
            }, 100L)
            findNavController().navigate(R.id.action_decibelFragment_to_recordFileFragment)
        }
    }          // onViewCreated

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDateTime(dateTimeStr: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return LocalDateTime.parse(dateTimeStr, formatter)
    }

    private fun getDongData(page: Int, callback: (List<SLResult>) -> Unit) {
        val soundDongData = SoundLevelSetup.service.getSoundLevelDong("101", null, page)
        soundDongData.enqueue(object : Callback<SoundLevel> {
            override fun onResponse(call: Call<SoundLevel>, response: Response<SoundLevel>) {
                if (response.isSuccessful) {
                    val soundLevels = response.body()
                    var results = soundLevels?.results ?: listOf()

                    // 현재 페이지가 마지막 페이지가 아닌 경우, 다음 페이지의 데이터 가져오기
                    if (soundLevels?.next != null) {
                        getDongData(page + 1) { nextResults ->
                            results += nextResults
                            callback(results)
                        }
                    } else {
                        callback(results)
                    }
                }
            }
            override fun onFailure(call: Call<SoundLevel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }) // soundLevel 데이터 가져오기
    }    // getDongData

    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawDongChart() {
        getDongData(1) { soundLevels ->
            if (soundLevels != null) {
                val averages = AveragePerPeriod(soundLevels)
                val entries = averages.mapIndexed { index, average ->
                    BarEntry(index.toFloat(), average)
                }
                setLimitDong(dongChart)

                val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                val userDong = sharedPref.getString("user_dong", null)

                val dongDataSet = BarDataSet(entries, "${userDong}동 시간별 평균 데시벨")
                dongDataSet.setColor(Color.parseColor("#C8BFE7"))
                dongDataSet.setValueTextSize(15f)
                val dongData = BarData(dongDataSet)
                dongData.barWidth = 0.6f


                val Donglegend = dongChart.legend
                Donglegend.textSize = 14f
                Donglegend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                Donglegend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                dongChart.description.isEnabled = false

                // X축 설정
                val DongxAxis = dongChart.xAxis
                DongxAxis.position = XAxis.XAxisPosition.BOTTOM // X축을 아래에 위치시킵니다.
                DongxAxis.setDrawAxisLine(true) // X축 선을 그립니다.
                DongxAxis.setDrawGridLines(true) // 그리드 선을 그리지 않습니다.
                // DongxAxis.setDrawLabels(false)                   // X축 레이블 제거
                DongxAxis.valueFormatter = IndexAxisValueFormatter(arrayListOf("", "", "", "", "", "", "")) // X축 레이블 설정
                DongxAxis.textSize = 14f
                DongxAxis.setLabelCount(7, true)

                // Y축 왼쪽 설정
                val DongyAxisLeft = dongChart.axisLeft
                DongyAxisLeft.setDrawAxisLine(true) // Y축 선을 그립니다.
                DongyAxisLeft.setDrawGridLines(true) // 그리드 선을 그리지 않습니다.
                DongyAxisLeft.axisMinimum = 30f

                // Y축 오른쪽 설정
                val DongyAxisRight = dongChart.axisRight
                DongyAxisRight.isEnabled = false // Y축 오른쪽 비활성화

                dongChart.data = dongData
                dongChart.invalidate() // refresh chart
            }
        }
    }         // drawDongChart

    private fun getHoData(page: Int, callback: (List<SLResult>) -> Unit) {
        val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        val soundDongData = SoundLevelSetup.service.getSoundLevelHome("Token $token", null, page)
        soundDongData.enqueue(object : Callback<SoundLevel> {
            override fun onResponse(call: Call<SoundLevel>, response: Response<SoundLevel>) {
                if (response.isSuccessful) {
                    val soundLevels = response.body()
                    var results = soundLevels?.results ?: listOf()

                    // 현재 페이지가 마지막 페이지가 아닌 경우, 다음 페이지의 데이터 가져오기
                    if (soundLevels?.next != null) {
                        getDongData(page + 1) { nextResults ->
                            results += nextResults
                            callback(results)
                        }
                    } else {
                        callback(results)
                    }
                }
            }
            override fun onFailure(call: Call<SoundLevel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        }) // soundLevel 데이터 가져오기
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun drawHoChart() {
        getHoData(1) { soundLevels ->
            if (soundLevels != null) {
                val averages = AveragePerPeriod(soundLevels)
                val entries = averages.mapIndexed { index, average ->
                    BarEntry(index.toFloat(), average)
                }

                val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                val userDong = sharedPref.getString("user_dong", null)
                val userHo = sharedPref.getString("user_ho", null)

                val hoDataSet = LineDataSet(entries, "${userDong}동 ${userHo}호 시간별 평균 데시벨")
                hoDataSet.setColor(Color.parseColor("#C8BFE7"))
                hoDataSet.setValueTextSize(15f)
                hoDataSet.lineWidth = 5f  // Set line width
                val hoData = LineData(hoDataSet)

                setLimitHo(hoChart)

                val Holegend = hoChart.legend
                Holegend.textSize = 14f
                Holegend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                Holegend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                hoChart.description.isEnabled = false

                // X축 설정
                val HoxAxis = hoChart.xAxis
                HoxAxis.position = XAxis.XAxisPosition.BOTTOM // X축을 아래에 위치시킵니다.
                HoxAxis.setDrawAxisLine(true) // X축 선을 그립니다.
                HoxAxis.setDrawGridLines(true) // 그리드 선을 그리지 않습니다.
                // HoxAxis.setDrawLabels(false)
                HoxAxis.valueFormatter = IndexAxisValueFormatter(arrayListOf("", "", "", "", "", "", "")) // X축 레이블 설정
                HoxAxis.textSize = 14f
                HoxAxis.setLabelCount(7, true)

                // Y축 왼쪽 설정
                val HoyAxisLeft = hoChart.axisLeft
                HoyAxisLeft.setDrawAxisLine(true) // Y축 선을 그립니다.
                HoyAxisLeft.setDrawGridLines(true) // 그리드 선을 그리지 않습니다.
                HoyAxisLeft.axisMinimum = 30f

                // Y축 오른쪽 설정
                val HoyAxisRight = hoChart.axisRight
                HoyAxisRight.isEnabled = false // Y축 오른쪽 비활성화

                hoChart.data = hoData
                hoChart.invalidate()  // refresh chart
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun AveragePerPeriod(data: List<SLResult>): FloatArray {
        val periodRanges = arrayOf(2..6, 6..10, 10..14, 14..18, 18..22, 22..26)
        val sums = FloatArray(periodRanges.size) { 0f }              // Initialize an array to hold the sum of values in each period
        val counts = IntArray(periodRanges.size) { 0 }               // Initialize an array to hold the count of values in each period

        for (soundLevel in data) {
            val dateTime = parseDateTime(soundLevel.created_at)
            val hour = if (dateTime.hour < 2) dateTime.hour + 24 else dateTime.hour
            val periodIndex = periodRanges.indexOfFirst { range -> hour in range }           // Compute the period index based on the hour of the timestamp

            sums[periodIndex] += soundLevel.value.toFloat()                                    // Update the sum and count for the period
            counts[periodIndex]++
        }

        val averages = FloatArray(periodRanges.size) { i -> if (counts[i] != 0) sums[i] / counts[i] else 0f }            // Compute the averages for each period
        return averages
    }

    fun setLimitDong(chart: BarChart) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val limitValue = if (currentHour >= 22 || currentHour < 6) 34f else 39f

        val ll = LimitLine(limitValue, "현재 층간소음 기준")
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.RED
        ll.textSize = 12f

        val leftAxis = chart.axisLeft
        // 이전에 추가된 LimitLine이 있다면 삭제
        leftAxis.removeAllLimitLines()
        // 새로운 LimitLine 추가
        leftAxis.addLimitLine(ll)
    }

    fun setLimitHo(chart: LineChart) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val limitValue = if (currentHour >= 22 || currentHour < 6) 34f else 39f

        val ll = LimitLine(limitValue, "현재 층간소음 기준")
        ll.lineColor = Color.RED
        ll.lineWidth = 2f
        ll.textColor = Color.RED
        ll.textSize = 12f

        val leftAxis = chart.axisLeft
        // 이전에 추가된 LimitLine이 있다면 삭제
        leftAxis.removeAllLimitLines()
        // 새로운 LimitLine 추가
        leftAxis.addLimitLine(ll)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}