package com.example.animalDev

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // 현재 좋아요 상태를 저장하는 Map
    private val likesStatus = mutableMapOf<String, Boolean>()

    // LiveData를 사용하여 UI에 상태 변화를 알림
    private val _likesLiveData = MutableLiveData<Map<String, Boolean>>()
    val likesLiveData: LiveData<Map<String, Boolean>> = _likesLiveData

    // 좋아요 상태 업데이트 함수
    fun updateLikeStatus(animalId: String, isLiked: Boolean) {
        // 상태 업데이트
        likesStatus[animalId] = isLiked

        // LiveData 업데이트하여 UI에 변경 알림
        _likesLiveData.value = likesStatus
    }

    // 특정 동물의 좋아요 상태를 조회하는 함수
    fun isLiked(animalId: String): Boolean {
        return likesStatus[animalId] ?: false
    }
}