package com.example.animaldev.data

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val response: ResponseBody
)

data class ResponseBody(
    val body: Body
)

data class Body(
    val items: Items
)

data class Items(
    val item: List<District>
)

data class District(
    @SerializedName("uprCd") val uprCd : String,
    @SerializedName("orgCd") val orgCd : String,
    @SerializedName("orgdownNm") val orgdownNm : String
) {
    override fun toString(): String {
        return orgdownNm
    }
}
