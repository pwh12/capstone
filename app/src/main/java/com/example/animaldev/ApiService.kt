package com.example.animalDev

import com.example.animalDev.data.AnimalApiResponse
import com.example.animalDev.data.GeocodingResponse
import com.example.animaldev.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("geocode/json")
    fun getLatLngFromAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Call<GeocodingResponse>

    @GET("sigungu")
    fun getDistricts(
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("_type") type: String = "json",
        @Query("upr_cd") cityCode: String
    ): Call<ApiResponse>

    // 지역코드 o
    @GET("abandonmentPublic")
    fun getAbandonedAnimals(
        @Query("upr_cd") uprCd: String,
        @Query("org_cd") orgCd: String,
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("_type") type: String = "json",
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1000,
    ): Call<AnimalApiResponse>

    // 지역코드 x
    @GET("abandonmentPublic")
    fun getAllAnimals(
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("_type") type: String = "json",
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1000
    ): Call<AnimalApiResponse>

    // 지역, 필터 적용위함
    @GET("abandonmentPublic")
    fun getFilteredAnimals(
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("_type") type: String = "json",
        @Query("upr_cd") uprCd: String,
        @Query("org_cd") orgCd: String,
        @Query("upkind") upkindCd: String,
        @Query("kind") kindCd: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1000
    ): Call<AnimalApiResponse>

}