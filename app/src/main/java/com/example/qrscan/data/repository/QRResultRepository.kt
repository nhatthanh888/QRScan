package com.example.qrscan.data.repository

import androidx.annotation.WorkerThread
import com.example.qrscan.data.dao.QRDao
import com.example.qrscan.data.model.ResultScanModel
import kotlinx.coroutines.flow.Flow

class QRResultRepository(private val qrDao: QRDao) {
    val listHistory: Flow<List<ResultScanModel>> = qrDao.getAllResult()
    val listScannedHistory: Flow<List<ResultScanModel>> = qrDao.getScannedResult(true)
    val listCreatedHistory: Flow<List<ResultScanModel>> = qrDao.getCreatedResult(true)
    val listFavorite: Flow<List<ResultScanModel>> = qrDao.getFavoriteResult(true)

    @WorkerThread
    suspend fun insertItem(resultScanModel: ResultScanModel) {
        qrDao.insertQR(resultScanModel)
    }

    //favorite
    @WorkerThread
    suspend fun updateFavorite(id: Int, favorite: Boolean) {
        qrDao.updateFavorite(id, favorite)
    }

    @WorkerThread
    suspend fun deleteFavorite(update:Boolean,favorite: Boolean, isCheck: Boolean) {
        qrDao.deleteFavorite(update,favorite, isCheck)
    }

    @WorkerThread
    suspend fun updateIsDeleteItemFavorite(isDelete: Boolean,favorite: Boolean) {
        qrDao.updateIsDeleteItemFavorite(isDelete,favorite)
    }

    @WorkerThread
    suspend fun updateIsCheckItemFavorite(isCheck: Boolean,favorite: Boolean) {
        qrDao.updateIsCheckItemFavorite(isCheck,favorite)
    }

    @WorkerThread
    suspend fun deleteFavoriteAll(update:Boolean,favorite: Boolean) {
        qrDao.deleteFavoriteAll(update,favorite)
    }

    @WorkerThread
    suspend fun updateCheckItemFavorite(id: Int, isCheck: Boolean, favorite: Boolean) {
        qrDao.updateCheckItemFavorite(id, isCheck, favorite)
    }


    //created
    @WorkerThread
    suspend fun deleteCreated(created: Boolean, isCheck: Boolean) {
        qrDao.deleteCreated(created, isCheck)
    }

    @WorkerThread
    suspend fun updateIsDeleteItemCreated(isDelete: Boolean,created: Boolean) {
        qrDao.updateIsDeleteItemCreated(isDelete,created)
    }

    @WorkerThread
    suspend fun updateIsCheckItemCreated(isCheck: Boolean,created:Boolean) {
        qrDao.updateIsCheckItemCreated(isCheck,created)
    }

    @WorkerThread
    suspend fun deleteCreatedAll(created: Boolean) {
        qrDao.deleteCreatedAll(created)
    }

    @WorkerThread
    suspend fun updateCheckItemCreated(id: Int, isCheck: Boolean, created: Boolean) {
        qrDao.updateCheckItemCreated(id, isCheck, created)
    }

    //scanned
    @WorkerThread
    suspend fun deleteScanned(scanned: Boolean, isCheck: Boolean) {
        qrDao.deleteScanned(scanned, isCheck)
    }

    @WorkerThread
    suspend fun updateIsDeleteItemScanned(isDelete: Boolean,scanned: Boolean) {
        qrDao.updateIsDeleteItemScanned(isDelete,scanned)
    }

    @WorkerThread
    suspend fun updateIsCheckItemScanned(isCheck: Boolean,scanned:Boolean) {
        qrDao.updateIsCheckItemScanned(isCheck,scanned)
    }

    @WorkerThread
    suspend fun deleteScannedAll(scanned: Boolean) {
        qrDao.deleteScannedAll(scanned)
    }

    @WorkerThread
    suspend fun updateCheckItemScanned(id: Int, isCheck: Boolean, scanned: Boolean) {
        qrDao.updateCheckItemScanned(id, isCheck, scanned)
    }


}