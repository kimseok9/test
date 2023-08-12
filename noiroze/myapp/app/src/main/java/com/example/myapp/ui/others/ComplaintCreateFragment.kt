package com.example.myapp.ui.others

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController

import com.example.myapp.R
import com.example.myapp.databinding.FragmentComplaintCreateBinding
import com.example.myapp.ui.others.service.CreateComplainRequest
import com.example.myapp.ui.others.service.NoticeComplain
import com.google.android.material.bottomsheet.BottomSheetDialog

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class ComplaintCreateFragment : Fragment() {

    private var _binding : FragmentComplaintCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentComplaintCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val complainTitle = binding.textSelectCategory
        val btnCancel = binding.buttonCancel
        val btnComplete = binding.buttonComplete
        val complainContent = binding.textComplainContent

        complainTitle.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.category_select_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            val listView = view.findViewById<ListView>(R.id.listView)
            val listItems = arrayListOf("불만접수", "신고접수", "건의사항") // Add more items as needed

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val selectedItem = listItems[position]
                complainTitle.text = selectedItem
                bottomSheetDialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            findNavController().navigateUp() // 이전 화면으로 돌아갑니다.
        }

        btnComplete.setOnClickListener {
            val title = complainTitle.text.toString()          // 카테고리를 입력
            val content = complainContent.text.toString()                   // 내용을 입력

            if (title.isNotEmpty() && content.isNotEmpty()) {                                          // 각 필드가 비어있지 않은지 확인
                val sharedPref = requireActivity().getSharedPreferences("LoginData", AppCompatActivity.MODE_PRIVATE)
                val token = sharedPref.getString("token", null)
                val author = sharedPref.getString("user_id", "") ?: ""
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
                format.timeZone = TimeZone.getTimeZone("GMT+9")
                val created_date = format.format(Date())
                val request = CreateComplainRequest(title, content, author, created_date)
                val complainCreate = NoticeComplain.compservice.createComplain("Token $token", request)
                complainCreate.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "민원이 접수되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()         // 요청이 성공하면 이전 화면으로 돌아갑니다.
                        } else {
                            val message = response.errorBody()?.string()         // 서버에서 에러 메시지를 받아와 토스트 메시지로 보여줍니다.
                            Log.d("게시글 작성 에러", "Error")
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("네트워크 에러 : ", t.message.toString())        // 요청이 실패하면 로그에 에러를 출력합니다.
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}