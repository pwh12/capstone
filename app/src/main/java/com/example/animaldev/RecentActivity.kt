package com.example.animalDev

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.animalDev.data.Animal
import com.example.animalDev.databinding.ActivityRecentBinding
import com.example.animalDev.databinding.AnimalListCardviewBinding


class RecentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val existingAnimals = mutableSetOf<String>() // 이미 생성된 동물의 noticeNo를 저장하는 Set

        val animals = databaseHelper.getRecentVisited()

        for (animal in animals) {
            if (!existingAnimals.contains(animal.noticeNo)) { // 이미 생성된 동물인지 확인
                existingAnimals.add(animal.noticeNo) // 생성된 동물이 아니면 추가
                addAnimalCardView(animal) // 카드뷰 추가
            }
        }
    }

    private fun addAnimalCardView(animal: Animal) {
        val newCardViewBinding = AnimalListCardviewBinding.inflate(layoutInflater, binding.resultLll, false)
        val secureUrl = animal.popfile.replace("http://", "https://")

        Glide.with(this)
            .load(secureUrl) // 불러올 이미지 url
            .into(newCardViewBinding.imgAnimal) // 이미지를 넣을 뷰

        newCardViewBinding.type.text = animal.kindCd
        newCardViewBinding.age.text = animal.age
        newCardViewBinding.noticeStart.text = animal.noticeSdt
        newCardViewBinding.noticeEnd.text = animal.noticeEdt

        if (animal.sexCd.equals("M")) {
            newCardViewBinding.sexType.setImageResource(R.drawable.gender_male)
        } else {
            newCardViewBinding.sexType.setImageResource(R.drawable.gender_female)
        }

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
            Toast.makeText(
                this,
                if (updatedIsLiked) "관심 목록에 추가되었습니다." else "관심 목록에서 제거되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 카드뷰 클릭 리스너
        newCardViewBinding.root.setOnClickListener { // 상세 정보 액티비티로 이동하는 인텐트 생성
            val intent = Intent(this, DetailActivity::class.java).apply {
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
            startActivity(intent)
        }

        binding.resultLll.addView(newCardViewBinding.root,0)
    }
}

