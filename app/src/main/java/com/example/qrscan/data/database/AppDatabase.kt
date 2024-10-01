package com.example.qrscan.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qrscan.data.dao.MultipleScanDao
import com.example.qrscan.data.dao.QRDao
import com.example.qrscan.data.model.MultipleScanModel
import com.example.qrscan.data.model.ResultScanModel

@Database(entities = [ResultScanModel::class, MultipleScanModel::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun multipleScanDao(): MultipleScanDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "QRScan"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}