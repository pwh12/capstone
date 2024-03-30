package com.example.animalDev

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecentDataCleanup : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 1시간 이후의 데이터를 삭제하는 작업 수행
        cleanupOldData()
        return START_STICKY
    }

    private fun cleanupOldData() {
        val currentTime = System.currentTimeMillis()
        val TimeLater = currentTime + 60 * 1000 // 시간설정

        val dbHelper = DatabaseHelper(applicationContext)
        val db = dbHelper.writableDatabase

        // 1시간 이후의 데이터를 삭제하는 쿼리 실행
        db.delete(DatabaseHelper.TABLE_NAME_DETAIL_VISITED, null, arrayOf(TimeLater.toString()))

        db.close()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}