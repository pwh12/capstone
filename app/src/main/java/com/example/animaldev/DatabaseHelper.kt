package com.example.animalDev

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.animalDev.data.Animal

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "liked_animals.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_NAME_DETAIL_VISITED = "detail_visited"
        private const val TABLE_NAME = "likes"
        private const val KEY_ID = "id"
        private const val KEY_IS_LIKED = "isLiked"

        private const val KEY_DESERTION_NO = "desertionNo"
        private const val KEY_FILENAME = "filename"
        private const val KEY_HAPPEN_DT = "happenDt"
        private const val KEY_HAPPEN_PLACE = "happenPlace"
        private const val KEY_KIND_CD = "kindCd"
        private const val KEY_COLOR_CD = "colorCd"
        private const val KEY_AGE = "age"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_NOTICE_NO = "noticeNo"
        private const val KEY_NOTICE_SDT = "noticeSdt"
        private const val KEY_NOTICE_EDT = "noticeEdt"
        private const val KEY_POPFILE = "popfile"
        private const val KEY_PROCESS_STATE = "processState"
        private const val KEY_SEX_CD = "sexCd"
        private const val KEY_NEUTER_YN = "neuterYn"
        private const val KEY_SPECIAL_MARK = "specialMark"
        private const val KEY_CARE_NM = "careNm"
        private const val KEY_CARE_TEL = "careTel"
        private const val KEY_CARE_ADDR = "careAddr"
        private const val KEY_ORG_NM = "orgNm"
        private const val KEY_CHARGE_NM = "chargeNm"
        private const val KEY_OFFICETEL = "officetel"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIKES_TABLE = ("CREATE TABLE $TABLE_NAME($KEY_ID TEXT PRIMARY KEY, $KEY_IS_LIKED INTEGER)")
        db?.execSQL(CREATE_LIKES_TABLE)

        val CREATE_DETAIL_VISITED_TABLE = ("CREATE TABLE $TABLE_NAME_DETAIL_VISITED(" +
                "$KEY_ID TEXT PRIMARY KEY, " +
                "$KEY_DESERTION_NO TEXT, " +
                "$KEY_FILENAME TEXT, " +
                "$KEY_HAPPEN_DT TEXT, " +
                "$KEY_HAPPEN_PLACE TEXT, " +
                "$KEY_KIND_CD TEXT, " +
                "$KEY_COLOR_CD TEXT, " +
                "$KEY_AGE TEXT, " +
                "$KEY_WEIGHT TEXT, " +
                "$KEY_NOTICE_NO TEXT, " +
                "$KEY_NOTICE_SDT TEXT, " +
                "$KEY_NOTICE_EDT TEXT, " +
                "$KEY_POPFILE TEXT, " +
                "$KEY_PROCESS_STATE TEXT, " +
                "$KEY_SEX_CD TEXT, " +
                "$KEY_NEUTER_YN TEXT, " +
                "$KEY_SPECIAL_MARK TEXT, " +
                "$KEY_CARE_NM TEXT, " +
                "$KEY_CARE_TEL TEXT, " +
                "$KEY_CARE_ADDR TEXT, " +
                "$KEY_ORG_NM TEXT, " +
                "$KEY_CHARGE_NM TEXT, " +
                "$KEY_OFFICETEL TEXT" +
                ")")
        db?.execSQL(CREATE_DETAIL_VISITED_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_DETAIL_VISITED")
        onCreate(db)
    }

    // 여기에 데이터 추가, 조회, 업데이트, 삭제 관련 메서드 추가
    fun addOrUpdateLike(animalId: String, isLiked: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, animalId)
        values.put(KEY_IS_LIKED, if (isLiked) 1 else 0)

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun isLiked(animalId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(KEY_IS_LIKED), // Columns you want to fetch
            "$KEY_ID=?", // Selection criteria
            arrayOf(animalId), // Selection args replacing ?
            null, // Group by
            null, // Having
            null  // Order by
        )
        var isLiked = false
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(KEY_IS_LIKED)
            if (columnIndex != -1) { // Check if the column index is valid
                isLiked = cursor.getInt(columnIndex) > 0
            }
        }
        cursor.close()
        db.close()
        return isLiked
    }

    fun removeLike(animalId: String) {
        val db = this.writableDatabase

        // 특정 animalId에 해당하는 좋아요 데이터를 삭제
        db.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(animalId))
        db.close()
    }

    // 최근 조회한 데이터를 테이블에 저장
    fun addRecentVisited(animal: Animal) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_DESERTION_NO, animal.desertionNo)
            put(KEY_FILENAME, animal.filename)
            put(KEY_HAPPEN_DT, animal.happenDt)
            put(KEY_HAPPEN_PLACE, animal.happenPlace)
            put(KEY_KIND_CD, animal.kindCd)
            put(KEY_COLOR_CD, animal.colorCd)
            put(KEY_AGE, animal.age)
            put(KEY_WEIGHT, animal.weight)
            put(KEY_NOTICE_NO, animal.noticeNo)
            put(KEY_NOTICE_SDT, animal.noticeSdt)
            put(KEY_NOTICE_EDT, animal.noticeEdt)
            put(KEY_POPFILE, animal.popfile)
            put(KEY_PROCESS_STATE, animal.processState)
            put(KEY_SEX_CD, animal.sexCd)
            put(KEY_NEUTER_YN, animal.neuterYn)
            put(KEY_SPECIAL_MARK, animal.specialMark)
            put(KEY_CARE_NM, animal.careNm)
            put(KEY_CARE_TEL, animal.careTel)
            put(KEY_CARE_ADDR, animal.careAddr)
            put(KEY_ORG_NM, animal.orgNm)
            put(KEY_CHARGE_NM, animal.chargeNm)
            put(KEY_OFFICETEL, animal.officetel)
        }

        db.insertWithOnConflict(TABLE_NAME_DETAIL_VISITED, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    // 최근 조회한 데이터들이 있는 테이블 조회
    @SuppressLint("Range")
    fun getRecentVisited(): List<Animal> {
        val db = this.readableDatabase
        val animals = mutableListOf<Animal>()

        val cursor = db.query(
            TABLE_NAME_DETAIL_VISITED, // 테이블 이름
            null, // 가져올 컬럼 배열, null일 경우 모든 컬럼을 가져옴
            null, // WHERE 절
            null, // WHERE 절에 전달될 값
            null, // GROUP BY
            null, // HAVING
            null // ORDER BY
        )

        cursor.use {
            while (it.moveToNext()) {
                val animal = Animal(
                    desertionNo = it.getString(it.getColumnIndex(KEY_DESERTION_NO)),
                    filename = it.getString(it.getColumnIndex(KEY_FILENAME)),
                    happenDt = it.getString(it.getColumnIndex(KEY_HAPPEN_DT)),
                    happenPlace = it.getString(it.getColumnIndex(KEY_HAPPEN_PLACE)),
                    kindCd = it.getString(it.getColumnIndex(KEY_KIND_CD)),
                    colorCd = it.getString(it.getColumnIndex(KEY_COLOR_CD)),
                    age = it.getString(it.getColumnIndex(KEY_AGE)),
                    weight = it.getString(it.getColumnIndex(KEY_WEIGHT)),
                    noticeNo = it.getString(it.getColumnIndex(KEY_NOTICE_NO)),
                    noticeSdt = it.getString(it.getColumnIndex(KEY_NOTICE_SDT)),
                    noticeEdt = it.getString(it.getColumnIndex(KEY_NOTICE_EDT)),
                    popfile = it.getString(it.getColumnIndex(KEY_POPFILE)),
                    processState = it.getString(it.getColumnIndex(KEY_PROCESS_STATE)),
                    sexCd = it.getString(it.getColumnIndex(KEY_SEX_CD)),
                    neuterYn = it.getString(it.getColumnIndex(KEY_NEUTER_YN)),
                    specialMark = it.getString(it.getColumnIndex(KEY_SPECIAL_MARK)),
                    careNm = it.getString(it.getColumnIndex(KEY_CARE_NM)),
                    careTel = it.getString(it.getColumnIndex(KEY_CARE_TEL)),
                    careAddr = it.getString(it.getColumnIndex(KEY_CARE_ADDR)),
                    orgNm = it.getString(it.getColumnIndex(KEY_ORG_NM)),
                    chargeNm = it.getString(it.getColumnIndex(KEY_CHARGE_NM)),
                    officetel = it.getString(it.getColumnIndex(KEY_OFFICETEL))
                )
                animals.add(animal)
            }
        }
        db.close()
        return animals
    }
}


