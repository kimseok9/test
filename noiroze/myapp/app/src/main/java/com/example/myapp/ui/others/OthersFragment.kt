package com.example.myapp.ui.others

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.myapp.R
import com.example.myapp.databinding.FragmentOthersBinding
import com.example.myapp.databinding.ItemOthersBinding
import com.example.myapp.ui.others.service.OthersItem


class OthersFragment : Fragment() {

    private var _binding : FragmentOthersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : OthersAdapter
    private val othersList = listOf (
        OthersItem("공지사항", R.drawable.megaphone, R.id.action_othersFragment_to_noticeFragment),
        OthersItem("민원접수", R.drawable.complaint, R.id.action_othersFragment_to_complaintFragment),
        OthersItem("나의민원 확인하기", R.drawable.check_complain, R.id.action_othersFragment_to_complainListFragment),
        OthersItem("관리실 연락하기", R.drawable.call_apart, R.id.action_othersFragment_to_complaintFragment),
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOthersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view:  View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OthersAdapter()
        binding.othersList.apply {
            adapter = this@OthersFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    inner class OthersAdapter : RecyclerView.Adapter<OthersFragment.OthersAdapter.OthersViewHolder>() {

        override fun getItemCount() = othersList.size

        inner class OthersViewHolder(val binding: ItemOthersBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OthersViewHolder {
            val binding = ItemOthersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return OthersViewHolder(binding)
        }

        override fun onBindViewHolder(holder: OthersFragment.OthersAdapter.OthersViewHolder, position: Int) {
            val eachOthersList = othersList[position]
            holder.binding.othersImage.setImageResource(eachOthersList.imageResId)
            holder.binding.othersTitle.text = eachOthersList.title

            holder.itemView.setOnClickListener {       // 목록에서 항목 클릭시, 여기서 _는 itemView의 View인데, 사용하지 않으므로 _로 처리.
                onItemClick(eachOthersList, position)                    // onItemClick 함수 사용. it은 위쪽의 imageList의 Result, position은 아이템의 위치.
            }
        }

        // 목록에서 항목을 클릭했을 때 호출되는 함수
        fun onItemClick(othersList: OthersItem, position: Int) {   // Result에서 가져온 imgFile이 목표, 몇번째 파일인지 position으로 확인을 함.
            if (othersList.title == "관리실 연락하기") {
                // 전화 앱 열기
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:" + "010-1234-5678") // 관리실 전화번호로 교체
                }
                startActivity(intent)
            }
            else {
                val bundle = Bundle().apply {
                    putString("others_title", othersList.title)
                }
                findNavController().navigate(othersList.actionId, bundle)
            }
        }
    }         // OthersAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}