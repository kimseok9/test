package com.example.myapp.ui.decibel

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.myapp.R

import com.example.myapp.databinding.FragmentRecordFileBinding
import com.example.myapp.databinding.ItemRecordFileBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RecordFileFragment : Fragment() {
    private var _binding : FragmentRecordFileBinding? = null
    private val binding get() = _binding!!

    private val recordFileList = mutableListOf<SFResult>()
    private lateinit var adapter: RecordFileAdapter
    private var currentPage = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecordFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecordFileAdapter()
        binding.recordFileListView.apply {
            adapter = this@RecordFileFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        getRecordFile(1)
    }

    private fun getRecordFile(page: Int) {
        val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        isLoading = true
        // api 호출 기본 골격 retrofit으로 서버에서 BoardList 목록을 가져옴
        val fileList = SoundLevelSetup.service.getSoundRecordFile("Token $token", page)
        fileList.enqueue(object : Callback<SoundFile> {
            override fun onResponse(call: Call<SoundFile>, response: Response<SoundFile>) {
                if(response.isSuccessful) {                                                                 // 성공했을 때. 즉, 네트워크 응답으로 데이터가 앱으로 전달될 때.
                    val fileLists = response.body()
                    if (isAdded && _binding != null) {                                                      // 프래그먼트가 실행 및 활성화되고(액티비티에 추가되었는지 확인), 바인딩이 null이 아닐 때
                        adapter.addData(fileLists!!.results)                                                              // adapter에 변경사항이 있음을 알려줌
                    }
                }
                isLoading = false
            }
            override fun onFailure(call: Call<SoundFile>, t: Throwable) {

            }
        })
    }

    inner class RecordFileAdapter : RecyclerView.Adapter<RecordFileAdapter.RecordFileViewHolder>() {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val targetFormat = SimpleDateFormat("M월 d일 H시 mm분", Locale.KOREAN)

        private fun formatDate(created_date: String): String {    // 날짜 형식을 변경하는 함수
            val date: Date = originalFormat.parse(created_date)
            return targetFormat.format(date)
        }

        fun addData(newData: List<SFResult>) {
            recordFileList.clear()
            recordFileList.addAll(newData)
            this.notifyDataSetChanged()
        }

        override fun getItemCount() = recordFileList.size

        inner class RecordFileViewHolder(val binding: ItemRecordFileBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordFileViewHolder {
            val binding = ItemRecordFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecordFileViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecordFileViewHolder, position: Int) {
            val eachFileList = recordFileList[position]
            holder.binding.recordFileName.text = eachFileList.file_name
            holder.binding.recordFileDate.text = formatDate(eachFileList.created_at)
            // holder.binding.recordFileDate.text = getTimeDifference(eachFileList.created_at)

            holder.itemView.setOnClickListener {
                onItemClick(eachFileList, position)
            }
        }

        // 목록에서 항목을 클릭했을 때 호출되는 함수
        fun onItemClick(eachFileList: SFResult, position: Int) {   // Result에서 가져온 eachFileList 가 몇번째 파일인지 position으로 확인을 함.
            val bundle = Bundle()
            bundle.putString("file_name", eachFileList.file_name)
            bundle.putString("sound_file", eachFileList.sound_file)
            findNavController().navigate(R.id.action_recordFileFragment_to_recordDetailFragment, bundle)
        }
    }         // RecordFileAdapter



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}