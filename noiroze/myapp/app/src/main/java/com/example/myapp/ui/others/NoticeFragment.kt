package com.example.myapp.ui.others

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.FragmentNoticeBinding
import com.example.myapp.databinding.ItemNoticeBoardBinding
import com.example.myapp.ui.others.service.NoticeBoards
import com.example.myapp.ui.others.service.NoticeComplain
import com.example.myapp.ui.others.service.NoticeResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NoticeFragment : Fragment() {

    private var _binding : FragmentNoticeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NoticeBoardAdapter

    private val noticeList = mutableListOf<NoticeResult>()

    private var currentPage = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNoticeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoticeBoardAdapter()
        binding.NoticeListView.apply {
            adapter = this@NoticeFragment.adapter                   // 멤버 변수의 어댑터 사용
            layoutManager = LinearLayoutManager(requireContext())   // 레이아웃 매니저 사용
        }

        binding.NoticeListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 1)) {
                    currentPage++
                    loadNoticeData(currentPage)
                }
            }
        })
        loadNoticeData(currentPage)

    } // onViewCreated

    private fun loadNoticeData(page : Int) {
        val noticeBoardList = NoticeComplain.noticeservice.requestNoticeList(1)
        noticeBoardList.enqueue(object : Callback<NoticeBoards>{
            override fun onResponse(call: Call<NoticeBoards>, response: Response<NoticeBoards>) {
                if(response.isSuccessful) {                                                                 // 성공했을 때. 즉, 네트워크 응답으로 데이터가 앱으로 전달될 때.
                    val noticeLists = response.body()
                    if (isAdded && _binding != null) {                                                      // 프래그먼트가 실행 및 활성화되고(액티비티에 추가되었는지 확인), 바인딩이 null이 아닐 때
                        noticeList.clear()                                                                       // 게시글 목록 초기화
                        noticeList.addAll(noticeLists!!.results)                                                             // adapter에 변경사항이 있음을 알려줌
                        adapter.notifyDataSetChanged()
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<NoticeBoards>, t: Throwable) {
                Log.e("네트워크 요청 실패", t.toString())    // Log의 error 출력
            }

        })
    }


    inner class NoticeBoardAdapter : RecyclerView.Adapter<NoticeBoardAdapter.NoticeBoardViewHolder>() {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val targetFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)

        private fun formatDate(created_date: String): String {    // 날짜 형식을 변경하는 함수
            val date: Date = originalFormat.parse(created_date)
            return targetFormat.format(date)
        }

        override fun getItemCount() = noticeList.size

        inner class NoticeBoardViewHolder(val binding: ItemNoticeBoardBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeBoardAdapter.NoticeBoardViewHolder {
            val binding = ItemNoticeBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NoticeBoardViewHolder(binding)
        }

        override fun onBindViewHolder(holder: NoticeBoardAdapter.NoticeBoardViewHolder, position: Int) {
            val eachNoticeList = noticeList[position]
            val formattedDate: String = formatDate(eachNoticeList.created_date)

            holder.binding.noticeTitle.text = eachNoticeList.title
            holder.binding.noticeCreatedDate.text = formattedDate

            holder.itemView.setOnClickListener {       // 목록에서 항목 클릭시, 여기서 _는 itemView의 View인데, 사용하지 않으므로 _로 처리.
                onItemClick(eachNoticeList, position)                    // onItemClick 함수 사용. it은 위쪽의 imageList의 Result, position은 아이템의 위치.
            }
        }

        fun onItemClick(eachNoticeList : NoticeResult, position :Int) {
            val bundle = Bundle()
            val formattedDate: String = formatDate(eachNoticeList.created_date)
            bundle.putString("title", eachNoticeList.title)
            bundle.putString("createdDate", formattedDate)
            bundle.putString("content", eachNoticeList.content)
        }
    } // NoticeBoardAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}