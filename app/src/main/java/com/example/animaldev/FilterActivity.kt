package com.example.animalDev
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.animalDev.R
import com.example.animalDev.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private var selectedFilter: String? = null

    private var selectedKindCode: String? = null
    private var selectedKindName: String? = null
    private var selectedBreedCode: String? = null
    private var selectedBreedName: String? = null


    private val kindCodeMap = mapOf("강아지" to "417000", "고양이" to "422400")

    // 코드
    private val filterMap = mapOf("골든 리트리버" to "000054", "그레이 하운드" to "000056", "그레이트 덴" to "000055",
                                    "푸들" to "000128", "포메라니안" to "000089", "치와와" to "000032",
                                    "제주개" to "000156", "시바" to "000100", "기타" to "000115",
                                    "메인쿤" to "000176", "브리티쉬 숏헤어" to "000181", "아비니시안" to "000193",
                                     "벵갈" to "000179", "노르웨이 숲" to "000170", "먼치킨" to "000175",
                                      "발리네즈" to "000177", "스핑크스" to "000189", "기타" to "000201")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fitter1 = intent.getStringExtra("fitter 1")
        val fitter2 = intent.getStringExtra("fitter 2")
        val fitter3 = intent.getStringExtra("fitter 3")

        // 강아지 선택
        binding.btnDog.setOnClickListener{

            // 만약 이미 선택된 필터가 보이고, 그 필터의 텍스트가 버튼의 텍스트와 같다면 필터를 제거
            if (binding.chosenFitter1.visibility == View.VISIBLE && binding.chosenFitter1.text == binding.btnDog.text) {
                binding.chosenFitter1.visibility = View.INVISIBLE
                binding.chosenFitter1.text = ""
                binding.btnDog.setBackgroundResource(R.drawable.filter_round) // 기본 배경으로 리셋

                // 강아지 필터가 선택되지 않았으므로 고양이 버튼을 다시 활성화
                enableCatButtons()
            } else {
                selectedKindCode = kindCodeMap["강아지"]  // 강아지 코드 매핑
                selectedKindName = "강아지"

                // 만약 아무 필터도 선택되지 않았다면, 강아지 필터를 설정
                binding.chosenFitter1.visibility = View.VISIBLE
                binding.chosenFitter1.setText(binding.btnDog.text)
                binding.btnDog.setBackgroundResource(R.drawable.choose_filter)

                // 고양이 선택 불가
                val catButtons = arrayOf(binding.btnCat, binding.catAbinisian, binding.catBalinese, binding.catBangol,
                    binding.catMainkun, binding.catMunchkin, binding.catNorwayForest,
                    binding.catShortHair, binding.catSphynx, binding.etcCat)
                // 모든 버튼을 활성화
                catButtons.forEach { button ->
                    button.isEnabled = false
                }
            }
        }

        // 고양이 선택
        binding.btnCat.setOnClickListener{

            // 만약 이미 선택된 필터가 보이고, 그 필터의 텍스트가 버튼의 텍스트와 같다면 필터를 제거
            if (binding.chosenFitter1.visibility == View.VISIBLE && binding.chosenFitter1.text == binding.btnCat.text) {
                binding.chosenFitter1.visibility = View.INVISIBLE
                binding.chosenFitter1.text = ""
                binding.btnCat.setBackgroundResource(R.drawable.filter_round) // 기본 배경으로 리셋

                // 강아지 필터가 선택되지 않았으므로 고양이 버튼을 다시 활성화
                enableDogButtons()
            } else {
                selectedKindCode = kindCodeMap["고양이"]  // 고양이 코드 매핑
                selectedKindName = "고양이"

                // 만약 아무 필터도 선택되지 않았다면, 강아지 필터를 설정
                binding.chosenFitter1.visibility = View.VISIBLE
                binding.chosenFitter1.setText(binding.btnCat.text)
                binding.btnCat.setBackgroundResource(R.drawable.choose_filter)

                // 강아지 선택 불가
                val dogButtons = arrayOf(binding.btnDog, binding.dogChiwawa, binding.dogGrateDen, binding.dogPudle,
                    binding.dogPomeranian, binding.dogSiba, binding.dogGreyHound,
                    binding.dogJejuDog, binding.dogRitriber, binding.etcDog)
                // 모든 버튼을 활성화
                dogButtons.forEach { button ->
                    button.isEnabled = false
                }
            }
        }

        // 성별 선택 - 물어보기


        // 강아지 버튼 클릭 리스너 설정
        val dogButtons = listOf(binding.dogChiwawa, binding.dogGrateDen, binding.dogPudle,
            binding.dogPomeranian, binding.dogSiba, binding.dogGreyHound,
            binding.dogJejuDog, binding.dogRitriber, binding.etcDog)


        // 고양이 버튼 클릭 리스너 설정
        val catButtons = listOf(binding.catAbinisian, binding.catBalinese, binding.catBangol,
            binding.catMainkun, binding.catMunchkin, binding.catNorwayForest,
            binding.catShortHair, binding.catSphynx, binding.etcCat)


        dogButtons.forEach { button ->
            button.setOnClickListener {
                handleFilterSelection((it as Button).text.toString(), dogButtons, catButtons, filterMap)
            }
        }

        catButtons.forEach { button ->
            button.setOnClickListener {
                handleFilterSelection((it as Button).text.toString(), catButtons, dogButtons, filterMap)
            }
        }

        // 적용하기
        binding.btnSetting.setOnClickListener {
            // 선택된 필터 코드를 인텐트에 넣고 결과를 설정
            val intent = Intent().apply {
                putExtra("selectedKindCode", selectedKindCode)
                putExtra("selectedKindName", selectedKindName)
                putExtra("selectedBreedCode", selectedBreedCode)
                putExtra("selectedBreedName", selectedBreedName)
            }
            setResult(RESULT_OK, intent)
            finish()
        }


        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun enableCatButtons() {
        val catButtons = arrayOf(binding.btnCat, binding.catAbinisian, binding.catBalinese, binding.catBangol,
            binding.catMainkun, binding.catMunchkin, binding.catNorwayForest,
            binding.catShortHair, binding.catSphynx, binding.etcCat)
        // 모든 버튼을 활성화
        catButtons.forEach { button ->
            button.isEnabled = true
        }

    }
    fun enableDogButtons() {
        val dogButtons = arrayOf(binding.btnDog, binding.dogChiwawa, binding.dogGrateDen, binding.dogPudle,
            binding.dogPomeranian, binding.dogSiba, binding.dogGreyHound,
            binding.dogJejuDog, binding.dogRitriber, binding.etcDog)
        // 모든 버튼을 활성화
        dogButtons.forEach { button ->
            button.isEnabled = true
        }
    }

    private fun handleFilterSelection(selected: String, currentTypeButtons: List<Button>, otherTypeButtons: List<View>, filterMap: Map<String, String>) {
        val currentTypeSelected = currentTypeButtons.any { it.text == selected }

        if (selectedBreedCode == selected && currentTypeSelected) {
            // 이미 선택된 필터를 다시 클릭했으므로 선택을 해제
            selectedBreedCode = null
            binding.chosenFitter2.visibility = View.INVISIBLE
            binding.chosenFitter2.text = ""
            currentTypeButtons.forEach { it.setBackgroundResource(R.drawable.filter_round) }
            // 다른 종류의 버튼들을 활성화
            otherTypeButtons.forEach { it.isEnabled = true }
        } else {
            // 새 필터를 선택합
            selectedBreedCode = filterMap[selected]
            selectedBreedName = selected
            binding.chosenFitter2.visibility = View.VISIBLE
            binding.chosenFitter2.text = selected
            currentTypeButtons.forEach {
                (it as? Button)?.let { button ->
                    button.setBackgroundResource(if (button.text == selected) R.drawable.choose_filter else R.drawable.filter_round)
                }
            }// 다른 종류의 버튼들을 비활성화
            otherTypeButtons.forEach { it.isEnabled = false }
        }
    }

    private fun getSelectedFilters(): List<String> {
        // 현재 선택된 필터들의 이름을 리스트로 반환
        return listOfNotNull(
            binding.chosenFitter1.text.toString(),
            binding.chosenFitter2.text.toString()
            // 필요하다면 추가적인 필터들을 이 리스트에 포함
        ).filter { it.isNotEmpty() }
    }
}