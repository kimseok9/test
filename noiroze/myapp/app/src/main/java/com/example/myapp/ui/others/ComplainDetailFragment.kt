package com.example.myapp.ui.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapp.R
import com.example.myapp.databinding.FragmentComplainDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ComplainDetailFragment : Fragment() {

    private var _binding : FragmentComplainDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentComplainDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title")
        val author = arguments?.getString("author")
        val content = arguments?.getString("content")
        val createdDate = arguments?.getString("created_date")

        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        val targetFormat = SimpleDateFormat("M월 d일 HH시 mm분", Locale.KOREAN)

        val date: Date = originalFormat.parse(createdDate)
        val formattedDate: String = targetFormat.format(date)

        binding.complainCategory.text = title
        binding.complainAuthor.text = author
        binding.complainContent.text = content
        binding.complainCreatedTime.text = formattedDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}