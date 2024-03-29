package com.example.animalDev

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "liked_animals.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_NAME_DETAIL_VISITED = "detail_visited"
        private const val TABLE_NAME = "likes"
        private const val KEY_ID = "id"
        private const val KEY_IS_LIKED = "isLiked"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIKES_TABLE = ("CREATE TABLE $TABLE_NAME($KEY_ID TEXT PRIMARY KEY, $KEY_IS_LIKED INTEGER)")
        db?.execSQL(CREATE_LIKES_TABLE)

        val CREATE_DETAIL_VISITED_TABLE = ("CREATE TABLE $TABLE_NAME_DETAIL_VISITED($KEY_ID TEXT PRIMARY KEY)")
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

    // 상세정보를 조회한 데이터를 테이블에 저장
    fun addDetailVisited(animalId: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, animalId)

        db.insertWithOnConflict(TABLE_NAME_DETAIL_VISITED, null, values, SQLiteDatabase.CONFLICT_REPLACE)
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

}


