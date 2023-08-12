package com.example.myapp.ui.community

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.myapp.R
import com.example.myapp.databinding.FragmentCommunityListBinding
import com.example.myapp.ui.community.service.Result
import com.example.myapp.databinding.ItemCommunityListBinding
import com.example.myapp.ui.community.ViewModel.CommunityListViewModel
import com.example.myapp.ui.community.service.getTimeDifference

class CommunityListFragment : Fragment() {
    private var _binding: FragmentCommunityListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CommunityListViewModel by viewModels()
    private val adapter = CommunityListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommunityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BoardlistView.apply {
            adapter = adapter                                       // 멤버 변수의 어댑터 사용
            layoutManager = LinearLayoutManager(requireContext())   // 레이아웃 매니저 사용
        }

        viewModel.loadBoardList()       // 게시판 목록 데이터 불러오기
        initButtons()                   // 카테고리 선택 버튼 설정

        viewModel.filteredBoardList.observe(viewLifecycleOwner) { newBoardList ->
            adapter.updateData(newBoardList)
        } // 게시판 목록의 변화를 관찰하여 RecyclerView를 업데이트
    } // onViewCreated


    inner class CommunityListAdapter : RecyclerView.Adapter<CommunityListAdapter.CommunityListViewHolder>() {
        private var boardList: List<Result> = emptyList()

        override fun getItemCount() = boardList.size

        inner class CommunityListViewHolder(val binding: ItemCommunityListBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityListViewHolder {
            val binding = ItemCommunityListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CommunityListViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CommunityListViewHolder, position: Int) {
            val eachBoardList = boardList[position]
            holder.binding.boardCategory.text = eachBoardList.category
            holder.binding.boardTitle.text = eachBoardList.title
            holder.binding.boardAuthor.text = eachBoardList.author
            holder.binding.boardCreated.text = getTimeDifference(eachBoardList.createdDate)
            holder.binding.boardLike.text = eachBoardList.like.size.toString()
            // holder.binding.boardReply.text = eachBoardList.reply

            holder.itemView.setOnClickListener {       // 목록에서 항목 클릭시, 여기서 _는 itemView의 View인데, 사용하지 않으므로 _로 처리.
                onItemClick(eachBoardList)                    // onItemClick 함수 사용. it은 위쪽의 imageList의 Result, position은 아이템의 위치.
            }
        }

        fun updateData(newBoardList: List<Result>) {
            boardList = newBoardList
            notifyDataSetChanged()
        }

        fun onItemClick(eachBoardList: Result) {   // Result에서 가져온 imgFile이 목표, 몇번째 파일인지 position으로 확인을 함.
            val bundle = Bundle()
            bundle.putString("title", eachBoardList.title)
            bundle.putString("category", eachBoardList.category)
            bundle.putString("author", eachBoardList.author)
            bundle.putString("createdDate", eachBoardList.createdDate.toString())
            bundle.putString("content", eachBoardList.content)
            findNavController().navigate(R.id.action_communityFragment_to_boardDetailFragment, bundle)
        } // 목록에서 항목을 클릭했을 때 호출되는 함수
    } // CommunityBoardAdapter

    private fun initButtons() {
        val btnAll = binding.btnAll
        val btnCommu = binding.btnCommu
        val btnInfo = binding.btnInfo
        val btnPraise = binding.btnPraise
        val btnShare = binding.btnShare
        val btnAddBoard = binding.btnAddBoard

        btnAll.setOnClickListener {
            val startColor = Color.parseColor("#FFFFFF")
            val endColor = Color.parseColor("#D1CFCF")
            val backgroundColorAnimator = ObjectAnimator.ofObject(btnAll, "backgroundColor", ArgbEvaluator(), startColor, endColor)
            backgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
            backgroundColorAnimator.start()

            btnAll.postDelayed({
                val revertBackgroundColorAnimator = ObjectAnimator.ofObject(btnAll, "backgroundColor", ArgbEvaluator(), endColor, startColor)
                revertBackgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
                revertBackgroundColorAnimator.start()
            }, 50L) // 지연시간 100ms (0.1초)

            viewModel.changeCategory("전체보기")
        }

        btnCommu.setOnClickListener {
            val startColor = Color.parseColor("#FFFFFF")
            val endColor = Color.parseColor("#D1CFCF")
            val backgroundColorAnimator = ObjectAnimator.ofObject(btnCommu, "backgroundColor", ArgbEvaluator(), startColor, endColor)
            backgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
            backgroundColorAnimator.start()

            btnCommu.postDelayed({
                val revertBackgroundColorAnimator = ObjectAnimator.ofObject(btnCommu, "backgroundColor", ArgbEvaluator(), endColor, startColor)
                revertBackgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
                revertBackgroundColorAnimator.start()
            }, 50L) // 지연시간 100ms (0.1초)

            viewModel.changeCategory("소통해요")
        }

        btnInfo.setOnClickListener {
            val startColor = Color.parseColor("#FFFFFF")
            val endColor = Color.parseColor("#D1CFCF")
            val backgroundColorAnimator = ObjectAnimator.ofObject(btnInfo, "backgroundColor", ArgbEvaluator(), startColor, endColor)
            backgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
            backgroundColorAnimator.start()

            btnInfo.postDelayed({
                val revertBackgroundColorAnimator = ObjectAnimator.ofObject(btnInfo, "backgroundColor", ArgbEvaluator(), endColor, startColor)
                revertBackgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
                revertBackgroundColorAnimator.start()
            }, 50L) // 지연시간 100ms (0.1초)

            viewModel.changeCategory("정보공유")
        }

        btnPraise.setOnClickListener {
            val startColor = Color.parseColor("#FFFFFF")
            val endColor = Color.parseColor("#D1CFCF")
            val backgroundColorAnimator = ObjectAnimator.ofObject(btnPraise, "backgroundColor", ArgbEvaluator(), startColor, endColor)
            backgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
            backgroundColorAnimator.start()

            btnPraise.postDelayed({
                val revertBackgroundColorAnimator = ObjectAnimator.ofObject(btnPraise, "backgroundColor", ArgbEvaluator(), endColor, startColor)
                revertBackgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
                revertBackgroundColorAnimator.start()
            }, 50L) // 지연시간 100ms (0.1초)

            viewModel.changeCategory("칭찬해요")
        }

        btnShare.setOnClickListener {
            val startColor = Color.parseColor("#FFFFFF")
            val endColor = Color.parseColor("#D1CFCF")
            val backgroundColorAnimator = ObjectAnimator.ofObject(btnShare, "backgroundColor", ArgbEvaluator(), startColor, endColor)
            backgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
            backgroundColorAnimator.start()

            btnShare.postDelayed({
                val revertBackgroundColorAnimator = ObjectAnimator.ofObject(btnShare, "backgroundColor", ArgbEvaluator(), endColor, startColor)
                revertBackgroundColorAnimator.duration = 100 // 변경 지속 시간 100ms
                revertBackgroundColorAnimator.start()
            }, 50L) // 지연시간 100ms (0.1초)

            viewModel.changeCategory("나눔해요")
        }

        btnAddBoard.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_boardCreateFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}