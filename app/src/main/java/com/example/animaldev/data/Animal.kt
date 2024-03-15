package com.example.animalDev.data

data class AnimalApiResponse(
    val response: AnimalResponse
)

data class AnimalResponse(
    val header: AnimalHeader,
    val body: AnimalBody
)

data class AnimalHeader(
    val reqNo: String,
    val resultCode: String,
    val resultMsg: String
)

data class AnimalBody(
    val items: AnimalItems,
    val numOfRows: Int = 500,
    val pageNo: Int,
    val totalCount: Int
)

data class AnimalItems(
    val item: List<Animal>
)

data class Animal(
    val desertionNo: String,   // 유기번호
    val filename: String,      // 접수번호
    val happenDt: String,      // 접수일
    val happenPlace: String,   // 발견날짜
    val kindCd: String,        // 품종
    val colorCd: String,       // 색상
    val age: String,           // 나이
    val weight: String,        // 몸무게
    val noticeNo: String,      // 공고번호
    val noticeSdt: String,     // 공고시작일
    val noticeEdt: String,     // 공고종료일
    val popfile: String,       // 사진
    val processState: String,  // 상태
    val sexCd: String,         // 성별
    val neuterYn: String,      // 중성화여부
    val specialMark: String,   // 특징
    val careNm: String,        // 보호소이름
    val careTel: String,       // 보호소 전화번호
    val careAddr: String,      // 보호소 주소
    val orgNm: String,         // 관할기관(지역)
    val chargeNm: String,      // 담당자
    val officetel: String      // 담당자 번호
)

