package com.example.myapp.ui.community

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.myapp.login.LoginActivity.ToastManager.showToast

import com.example.myapp.R
import com.example.myapp.databinding.FragmentBoardCreateBinding
import com.example.myapp.ui.community.service.CommunityBoardSetup
import com.example.myapp.ui.community.service.CreatePostRequest
import com.google.android.material.bottomsheet.BottomSheetDialog

import java.text.SimpleDateFormat
import java.util.*

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardCreateFragment : Fragment() {

    private var _binding : FragmentBoardCreateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBoardCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn_cancel = binding.btnCancel
        val btn_complete = binding.btnComplete
        val txt_select_category = binding.txtSelectCategory
        val txt_title = binding.txtTitle
        val txt_content = binding.txtContent

        txt_select_category.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.category_select_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            val listView = view.findViewById<ListView>(R.id.listView)
            val listItems = arrayListOf("정보공유", "소통해요", "칭찬해요", "나눔해요") // Add more items as needed

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                val selectedItem = listItems[position]
                txt_select_category.text = selectedItem
                bottomSheetDialog.dismiss()
            }
        }

        btn_cancel.setOnClickListener {
            findNavController().navigateUp() // 이전 화면으로 돌아갑니다.
        }

        btn_complete.setOnClickListener{
            val category = txt_select_category.text.toString()          // 카테고리를 입력
            val title = txt_title.text.toString()                       // 제목을 입력
            val content = txt_content.text.toString()                   // 내용을 입력

            if (category.isNotEmpty() && title.isNotEmpty() && content.isNotEmpty()) {                                          // 각 필드가 비어있지 않은지 확인
                val sharedPref = requireActivity().getSharedPreferences("LoginData", AppCompatActivity.MODE_PRIVATE)
                val authorId = sharedPref.getString("user_id", "") ?: ""
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                format.timeZone = TimeZone.getTimeZone("UTC")
                val created_date = format.format(Date())
                val request = CreatePostRequest(category, title, content, authorId, created_date)                      // author와 created_date는 서버에서 처리
                val boardCreateCall = CommunityBoardSetup.service.createPost(request)                            // Retrofit 서비스를 이용해 서버에 POST 요청을 보냅니다.
                boardCreateCall.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            showToast(context, "게시글이 작성되었습니다.")
                            findNavController().navigateUp()         // 요청이 성공하면 이전 화면으로 돌아갑니다.
                        }
                        else {
                            val message = response.errorBody()?.string()         // 서버에서 에러 메시지를 받아와 토스트 메시지로 보여줍니다.
                            Log.d("BoardCreateError", "Error")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("BoardCreate Error: ", t.message.toString())        // 요청이 실패하면 로그에 에러를 출력합니다.
                    }
                })
            }
            else {
                Toast.makeText(context, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()        // 필드 중 하나라도 비어있다면 사용자에게 알립니다.
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}