package com.example.myapp.ui.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapp.databinding.FragmentBoardDetailBinding

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BoardDetailFragment : Fragment() {

    private var _binding: FragmentBoardDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBoardDetailBinding.inflate(inflater, container, false)

        val title = arguments?.getString("title")
        val category = arguments?.getString("category")
        val author = arguments?.getString("author")
        val createdDate = arguments?.getString("createdDate")
        val content = arguments?.getString("content")

        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val targetFormat = SimpleDateFormat("M월 d일 HH시 mm분", Locale.KOREAN)

        val date: Date = originalFormat.parse(createdDate)
        val formattedDate: String = targetFormat.format(date)

        binding.detailCategory.text = category
        binding.detailAuthor.text = author
        binding.detailCreatedTime.text = formattedDate
        binding.detailContent.text = content

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}