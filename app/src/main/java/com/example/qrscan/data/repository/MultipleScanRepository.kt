package com.example.qrscan.data.repository

import androidx.annotation.WorkerThread
import com.example.qrscan.data.dao.MultipleScanDao
import com.example.qrscan.data.model.MultipleScanModel
import kotlinx.coroutines.flow.Flow

class MultipleScanRepository(private val multipleScanDao: MultipleScanDao) {
    val listMultipleScan: Flow<List<MultipleScanModel>> = multipleScanDao.getAllResult()

    @WorkerThread
    suspend fun insertItem(multipleScanModel: MultipleScanModel) {
        multipleScanDao.insertMultipleScan(multipleScanModel)
    }

    @WorkerThread
    suspend fun deleteItem(isDelete: Boolean) {
        multipleScanDao.delete(isDelete)
    }

    @WorkerThread
    suspend fun updateIsDeleteItem(isDelete: Boolean) {
        multipleScanDao.updateIsDeleteItem(isDelete)
    }

    @WorkerThread
    suspend fun updateCheckItem(id:Int,isCheck: Boolean) {
        multipleScanDao.updateCheckItem(id,isCheck)
    }

    @WorkerThread
    suspend fun updateIsCheckItem(isCheck: Boolean) {
        multipleScanDao.updateIsCheckItem(isCheck)
    }

    @WorkerThread
    suspend fun deleteAllItem() {
        multipleScanDao.deleteAll()
    }
}