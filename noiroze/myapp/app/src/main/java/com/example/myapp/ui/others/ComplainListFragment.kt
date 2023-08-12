package com.example.myapp.ui.others

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R

import com.example.myapp.databinding.FragmentComplainListBinding
import com.example.myapp.databinding.ItemComplainDetailBinding
import com.example.myapp.ui.others.service.ComplainBoards
import com.example.myapp.ui.others.service.ComplainResult
import com.example.myapp.ui.others.service.NoticeComplain

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class ComplainListFragment : Fragment() {

    private var _binding : FragmentComplainListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : ComplainListAdapter

    private val complainList = mutableListOf<ComplainResult>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentComplainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ComplainListAdapter()
        binding.complainListView.apply {
            adapter = this@ComplainListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        getMyComplain(1)
    }      // onViewCreated


    private fun getMyComplain(page : Int) {
        val sharedPref = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        val compList = NoticeComplain.compservice.requestComplainList("Token $token", page)
        compList.enqueue(object : Callback<ComplainBoards>{
            override fun onResponse(call: Call<ComplainBoards>, response: Response<ComplainBoards>) {
                if (response.isSuccessful) {
                    val Lists = response.body()
                    adapter.addData(Lists!!.results)
                }
            }
            override fun onFailure(call: Call<ComplainBoards>, t: Throwable) {

            }
        })
    }

    inner class ComplainListAdapter : RecyclerView.Adapter<ComplainListAdapter.ComplainListViewHolder>() {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        val targetFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)

        private fun formatDate(created_date: String): String {    // 날짜 형식을 변경하는 함수
            val date: Date = originalFormat.parse(created_date)
            return targetFormat.format(date)
        }

        fun addData(newData: List<ComplainResult>) {
            complainList.clear()
            complainList.addAll(newData)
            this.notifyDataSetChanged()
        }

        override fun getItemCount() = complainList.size

        inner class ComplainListViewHolder(val binding: ItemComplainDetailBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplainListFragment.ComplainListAdapter.ComplainListViewHolder {
            val binding = ItemComplainDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ComplainListViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ComplainListViewHolder, position : Int) {
            val eachCompList = complainList[position]
            val formattedDate: String = formatDate(eachCompList.created_date)

            holder.binding.complainTitle.text = eachCompList.title
            holder.binding.complainCreated.text = formattedDate

            holder.itemView.setOnClickListener {
                onItemClick(eachCompList, position)
            }
        }

        // 목록에서 항목을 클릭했을 때 호출되는 함수
        fun onItemClick(eachCompList: ComplainResult, position: Int) {   // Result에서 가져온 eachFileList 가 몇번째 파일인지 position으로 확인을 함.
            val bundle = Bundle()
            bundle.putString("title", eachCompList.title)
            bundle.putString("author", eachCompList.author)
            bundle.putString("content", eachCompList.content)
            bundle.putString("created_date", eachCompList.created_date)

            findNavController().navigate(R.id.action_complainListFragment_to_complainDetailFragment, bundle)
        }
    }        // ComplainListAdapter


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}