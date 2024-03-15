package com.example.animalDev

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.animalDev.BuildConfig
import com.example.animalDev.databinding.ActivityLocationBinding
import com.example.animaldev.data.ApiResponse
import com.example.animaldev.data.District
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationBinding

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/1543061/abandonmentPublicSrvc/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
    private val apiService by lazy { retrofit.create(ApiService::class.java) }

    private lateinit var citySpinner: Spinner
    private lateinit var locationSpinner: Spinner

    // 선택된 지역의 uprCd와 orgCd를 저장할 변수
    private var selectedUprNm: String? = null
    private var selectedOrgNm: String? = null
    private var selectedUprCd: String? = null
    private var selectedOrgCd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citySpinner = binding.spinnerArea   // 도시 선택 스피너
        locationSpinner = binding.spinnerGungu  // 지역 선택 스피너

        val items = arrayOf("서울특별시", "경기도", "부산광역시", "강원특별자치도", "대구광역시", "충청북도", "인천광역시", "충청남도", "광주광역시", "전북특별자치도", "세종특별자치시", "전라남도",
                            "대전광역시", "울산광역시", "경상남도", "경상북도", "제주특별자치도")

        val orgCds = arrayOf("6110000", "6410000", "6260000", "6530000", "6270000", "6430000", "6280000", "6440000", "6290000", "6540000", "5690000", "6460000",
                             "6300000", "6310000", "6480000","6470000", "6500000")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedUprNm = items[position]
                val selectedCityCode = orgCds[position] // 선택된 도시 코드
                fetchDistricts(selectedCityCode) /*{ districts -> updateDistrictSpinner(districts) }*/
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때의 동작
            }
        }

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 선택된 아이템의 uprCd와 orgCd를 저장
                val selectedItem = parent?.getItemAtPosition(position) as? District // API 응답을 기반으로 캐스팅, 필요에 따라 수정

                if (selectedItem != null) {
                    selectedUprCd = selectedItem.uprCd
                    selectedOrgCd = selectedItem.orgCd
                    selectedOrgNm = selectedItem.orgdownNm
                    Log.d("LocationActivity", "Selected district: $selectedUprCd, $selectedOrgCd, $selectedOrgNm")
                } else {
                    Log.d("LocationActivity", "Selected item is not a District object.")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // 설정하기 버튼 클릭
        binding.btnSetting.setOnClickListener {
            // 선택된 uprCd와 orgCd를 프래그먼트에 전달
//            val bundle = Bundle().apply {
//                putString("uprCd", selectedUprCd)
//                putString("orgCd", selectedOrgCd)
//                putString("fullCity", selectedUprNm + selectedOrgNm)
//            }
//            val fragment = HomeFragment().apply {
//                arguments = bundle
//            }
            Intent().apply {
                putExtra("uprCd", selectedUprCd)
                putExtra("orgCd", selectedOrgCd)
                putExtra("fullCity", "$selectedUprNm $selectedOrgNm")
            }.also { resultIntent ->
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // 액티비티 종료
            }

            Log.d("LocationActivity", "Setting result: $selectedUprCd, $selectedOrgCd, $selectedUprNm $selectedOrgNm")
        }

        // 뒤로가기 클릭
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun fetchDistricts(cityCode: String/*, callback: (List<String>) -> Unit*/) {
        val call = apiService.getDistricts(BuildConfig.API_KEY, cityCode = cityCode)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val districts = response.body()?.response?.body?.items?.item ?: emptyList()/*.map { it.orgdownNm }*/
                    runOnUiThread {
                        districts?.let {
                            // 스피너 업데이트
                            updateDistrictSpinner(districts)
                        }
                    }
                }
                else {
                    Toast.makeText(this@LocationActivity, "API 호출에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(this@LocationActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 지역 스피너 업데이트 함수
    fun updateDistrictSpinner(districts: List<District>) {
        val adapter = ArrayAdapter(this@LocationActivity, android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter
    }
}