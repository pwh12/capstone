package com.example.animalDev

import android.app.Activity
import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.animalDev.data.Animal
import com.example.animalDev.data.AnimalApiResponse
import com.example.animalDev.databinding.AnimalListCardviewBinding
import com.example.animalDev.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseHelper: DatabaseHelper
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/1543061/abandonmentPublicSrvc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService by lazy { retrofit.create(ApiService::class.java) }

    private lateinit var locationResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var filterResultLauncher: ActivityResultLauncher<Intent>
    private var uprCd : String? = null
    private var orgCd : String? = null
    private var upkindCd : String? = null  // 축종코드
    private var kindCd : String? = null    // 품종코드
    private var upkindNm : String? = null  // 축종이름
    private var kindNm : String? = null    // 품종이름

    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // DatabaseHelper 인스턴스 초기화
        databaseHelper = DatabaseHelper(requireContext())

        locationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 결과 받기
                val data: Intent? = result.data
                uprCd = data?.getStringExtra("uprCd")
                orgCd = data?.getStringExtra("orgCd")
                val cityNm = data?.getStringExtra("fullCity")

                Log.d("HomeFragment", "Received result: $uprCd, $orgCd, $cityNm")

                binding.textView3.setText(cityNm)
                binding.textView3.visibility = View.VISIBLE

                // 동물 상세 조회를 실행
                uprCd?.let { uprCd ->
                    orgCd?.let { orgCd ->
                        // fetchAbandonedAnimals(uprCd, orgCd)
                        // 동물 목록 업데이트 호출
                        fetchFilteredAnimals()
                    }
                }

            }
        }

        filterResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK){
                // 결과 받기
                val data: Intent? = result.data

                // 필터 적용
                upkindCd = data?.getStringExtra("selectedKindCode")
                kindCd = data?.getStringExtra("selectedBreedCode")
                upkindNm = data?.getStringExtra("selectedKindName")
                kindNm = data?.getStringExtra("selectedBreedName")

//                binding.chosenFitter1.visibility = View.VISIBLE
//                binding.chosenFitter1.text = upkindNm.toString()
//
//                binding.chosenFitter2.visibility = View.VISIBLE
//                binding.chosenFitter2.text = kindNm.toString()

                // 선택한 필터를 UI에 표시
                updateFilterDisplay(upkindNm, kindNm)

                // 동물 목록 업데이트 호출
                fetchFilteredAnimals()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 앱 시작 시 전체 데이터 로드
        fetchAllAnimals()

        // 지역 선택 화면 이동
        binding.selectCity.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java)
            locationResultLauncher.launch(intent)
        }

        // 필터 선택 화면 이동
        binding.selectFilter.setOnClickListener {
            val intent = Intent(context, FilterActivity::class.java)
            filterResultLauncher.launch(intent)
        }

        return binding.root
    }

    // 지역 코드 없이 전체 데이터를 불러오는 메소드
    private fun fetchAllAnimals() {
        apiService.getAllAnimals(BuildConfig.API_KEY).enqueue(object :
            Callback<AnimalApiResponse> {
            // onResponse와 onFailure의 구현은 동일
            override fun onResponse(call: Call<AnimalApiResponse>, response: Response<AnimalApiResponse>) {
                if (response.isSuccessful) {
                    val animals = response.body()?.response?.body?.items?.item ?: emptyList()

                    if(animals != null){
                        updatedAnimalList(animals)
                    }
                    else{
                        Toast.makeText(context, "리스트가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("HomeFragment", "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<AnimalApiResponse>, t: Throwable) {
                Log.e("HomeFragment", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 지역별 동물리스트 조회
    private fun fetchAbandonedAnimals(uprCd: String, orgCd: String) {
        apiService.getAbandonedAnimals(uprCd, orgCd, BuildConfig.API_KEY, "json").enqueue(object :
            Callback<AnimalApiResponse> {
            override fun onResponse(call: Call<AnimalApiResponse>, response: Response<AnimalApiResponse>) {
                if (response.isSuccessful) {
                    val animals = response.body()?.response?.body?.items?.item ?: emptyList()

                    if(animals != null){
                        updatedAnimalList(animals)
                    }
                    else{
                        Toast.makeText(context, "리스트가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("HomeFragment", "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<AnimalApiResponse>, t: Throwable) {
                Log.e("HomeFragment", "네트워크 오류: ${t.message}")
            }
        })
    }

    private fun updatedAnimalList(animals: List<Animal>) {
        binding.resultLl.removeAllViews() // 기존 뷰 제거
        val layoutInflater = LayoutInflater.from(context)

        for (animal in animals) {
            val newCardViewBinding = AnimalListCardviewBinding.inflate(layoutInflater, binding.resultLl, false)
            val secureUrl = animal.popfile.replace("http://", "https://")

            Glide.with(this)
                .load(secureUrl) // 불러올 이미지 url
                .into(newCardViewBinding.imgAnimal) // 이미지를 넣을 뷰

            newCardViewBinding.type.text = animal.kindCd
            newCardViewBinding.age.text = animal.age
            newCardViewBinding.noticeStart.text = animal.noticeSdt
            newCardViewBinding.noticeEnd.text = animal.noticeEdt

            if(animal.sexCd.equals("M")){
                newCardViewBinding.sexType.setImageResource(R.drawable.gender_male)
            }
            else{
                newCardViewBinding.sexType.setImageResource(R.drawable.gender_female)
            }
            // 1. 좋아요 생성 (상세 조회 화면과 동기화)
//            // 좋아요 상태에 따른 아이콘 설정
//            newCardViewBinding.imgHeart.setImageResource(if (isLiked) R.drawable.full_heart else R.drawable.heart)
//
//            // 좋아요 버튼 클릭 이벤트 처리
//            newCardViewBinding.imgHeart.setOnClickListener {
//                val newStatus = !isLiked
//                databaseHelper.addOrUpdateLike(animal.noticeNo, newStatus) // 상태 업데이트
//                newCardViewBinding.imgHeart.setImageResource(if (newStatus) R.drawable.full_heart else R.drawable.heart) // UI 업데이트
//
//                Toast.makeText(context, "관심 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()
//            }

//            2.
//            val isLiked = databaseHelper.isLiked(animal.noticeNo) // 데이터베이스에서 좋아요 상태 조회
//            newCardViewBinding.imgHeart.setOnClickListener {
//                if(isLiked){  // 좋아요가 눌러져 있는 상태라면
//                    // 좋아요 취소 처리
//                    databaseHelper.removeLike(animal.noticeNo)
//                    newCardViewBinding.imgHeart.setImageResource(R.drawable.heart) // UI 업데이트: 좋아요 취소 아이콘으로 변경
//                    Log.d("HomeFragment", "Removed like for animalId: ${animal.noticeNo}") // 로그 추가
//                    Toast.makeText(context, "관심 목록에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    // 좋아요 상태가 아닌 경우, 좋아요 처리
//                    databaseHelper.addOrUpdateLike(animal.noticeNo, true)
//                    newCardViewBinding.imgHeart.setImageResource(R.drawable.full_heart) // UI 업데이트: 좋아요 아이콘으로 변경
//                    Log.d("HomeFragment", "Added like for animalId: ${animal.noticeNo}") // 로그 추가
//                    Toast.makeText(context, "관심 목록에 추가되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }

            // 좋아요가 눌러져있는지 확인
            val isLiked = databaseHelper.isLiked(animal.noticeNo)
            Log.d("HomeFragment", "Animal ID ${animal.noticeNo} is liked: $isLiked")
            newCardViewBinding.imgHeart.setImageResource(if (isLiked) R.drawable.full_heart else R.drawable.heart)


            newCardViewBinding.imgHeart.setOnClickListener {
                val currentIsLiked = databaseHelper.isLiked(animal.noticeNo)
                if (currentIsLiked) {
                    // 좋아요 상태 취소
                    databaseHelper.removeLike(animal.noticeNo)
                } else {
                    // 좋아요 상태 설정
                    databaseHelper.addOrUpdateLike(animal.noticeNo, true)
                    Log.d("HomeFragment", "Like added for animalId: ${animal.noticeNo}")
                }
                // 상태 변경 후 UI 즉시 업데이트
                val updatedIsLiked = !currentIsLiked
                newCardViewBinding.imgHeart.setImageResource(if (updatedIsLiked) R.drawable.full_heart else R.drawable.heart)
                Toast.makeText(context, if (updatedIsLiked) "관심 목록에 추가되었습니다." else "관심 목록에서 제거되었습니다.", Toast.LENGTH_SHORT).show()
            }

            // 카드뷰 클릭 리스너
            newCardViewBinding.root.setOnClickListener { // 상세 정보 액티비티로 이동하는 인텐트 생성
                val intent = Intent(activity, DetailActivity::class.java).apply {
                    // 인텐트에 동물의 상세 정보를 넣습니다.
                    putExtra("noticeNo", animal.noticeNo)
                    putExtra("image", animal.popfile)
                    putExtra("type", animal.kindCd)
                    putExtra("age", animal.age)
                    putExtra("sex", animal.sexCd)
                    putExtra("color", animal.colorCd)
                    putExtra("weight", animal.weight)
                    putExtra("neuter", animal.neuterYn)
                    putExtra("happendAt", animal.happenDt)
                    putExtra("place", animal.happenPlace)
                    putExtra("startDate", animal.noticeSdt)
                    putExtra("endDate", animal.noticeEdt)
                    putExtra("specialMark", animal.specialMark)
                    putExtra("shelterNm", animal.careNm)
                    putExtra("shelterAddr", animal.careAddr)
                    putExtra("shelterTel", animal.careTel)
                }
                startActivity(intent) }

            binding.resultLl.addView(newCardViewBinding.root)
        }
    }

    //
    private fun fetchFilteredAnimals() {
        val call = when {
            // 지역과 필터 모두 선택한 경우
            !uprCd.isNullOrEmpty() && !upkindCd.isNullOrEmpty() && !kindCd.isNullOrEmpty() ->
                apiService.getFilteredAnimals(BuildConfig.API_KEY, "json", uprCd!!, orgCd!!, upkindCd!!, kindCd!!)

            // 지역만 선택한 경우
            !uprCd.isNullOrEmpty() ->
                apiService.getFilteredAnimals(BuildConfig.API_KEY, "json", uprCd!!, orgCd!!, "", "")

            // 필터만 선택한 경우
            !upkindCd.isNullOrEmpty() && !kindCd.isNullOrEmpty() ->
                apiService.getFilteredAnimals(BuildConfig.API_KEY, "json", "", "", upkindCd!!, kindCd!!)

            // 아무것도 선택하지 않은 경우 (전체 데이터)
            else ->
                apiService.getFilteredAnimals(BuildConfig.API_KEY, "json", "", "", "", "")
        }

        call.enqueue(object : Callback<AnimalApiResponse> {
            override fun onResponse(call: Call<AnimalApiResponse>, response: Response<AnimalApiResponse>) {
                if (response.isSuccessful) {
                    val animals = response.body()?.response?.body?.items?.item ?: emptyList()
                    updatedAnimalList(animals)
                } else {
                    Log.e("HomeFragment", "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<AnimalApiResponse>, t: Throwable) {
                Log.e("HomeFragment", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 필터 선택 or 취소 시 홈 상단 뷰 업데이트 함수
    private fun updateFilterDisplay(selectedKindName: String?, selectedBreedName: String?) {
        if (selectedKindName == null) {
            binding.chosenFitter1.visibility = View.GONE
        } else {
            binding.chosenFitter1.visibility = View.VISIBLE
            binding.chosenFitter1.text = selectedKindName
        }

        if (selectedBreedName == null) {
            binding.chosenFitter2.visibility = View.GONE
        } else {
            binding.chosenFitter2.visibility = View.VISIBLE
            binding.chosenFitter2.text = selectedBreedName
        }
    }
}