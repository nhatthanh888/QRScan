package com.example.qrscan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qrscan.data.model.ResultScanModel
import kotlinx.coroutines.flow.Flow

@Dao
interface QRDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQR(resultScanModel: ResultScanModel)

    //favorite
    @Query("UPDATE resultScanModel SET favorite = :update WHERE favorite =:favorite AND isCheck=:isCheck")
    suspend fun deleteFavorite(update: Boolean, favorite: Boolean, isCheck: Boolean)

    @Query("UPDATE resultScanModel SET isDelete = :isDelete WHERE favorite=:favorite")
    suspend fun updateIsDeleteItemFavorite(isDelete: Boolean, favorite: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE favorite=:favorite")
    suspend fun updateIsCheckItemFavorite(isCheck: Boolean,favorite: Boolean)

    @Query("UPDATE resultScanModel SET favorite = :update WHERE favorite =:favorite")
    suspend fun deleteFavoriteAll(update: Boolean,favorite: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE id=:id AND favorite=:favorite")
    suspend fun updateCheckItemFavorite(id: Int, isCheck: Boolean, favorite: Boolean)


    //create
    @Query("DELETE FROM resultScanModel WHERE created =:created AND isCheck=:isCheck")
    suspend fun deleteCreated(created: Boolean, isCheck: Boolean)

    @Query("UPDATE resultScanModel SET isDelete = :isDelete WHERE created=:created")
    suspend fun updateIsDeleteItemCreated(isDelete: Boolean, created: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE created=:created")
    suspend fun updateIsCheckItemCreated(isCheck: Boolean, created: Boolean)

    @Query("DELETE FROM resultScanModel WHERE created=:created")
    suspend fun deleteCreatedAll(created: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE id=:id AND created=:created")
    suspend fun updateCheckItemCreated(id: Int, isCheck: Boolean, created: Boolean)


    //scanned
    @Query("DELETE FROM resultScanModel WHERE scanned =:scanned AND isCheck=:isCheck")
    suspend fun deleteScanned(scanned: Boolean, isCheck: Boolean)

    @Query("UPDATE resultScanModel SET isDelete = :isDelete WHERE scanned=:scanned")
    suspend fun updateIsDeleteItemScanned(isDelete: Boolean, scanned: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE scanned=:scanned")
    suspend fun updateIsCheckItemScanned(isCheck: Boolean, scanned: Boolean)

    @Query("DELETE FROM resultScanModel WHERE scanned=:scanned")
    suspend fun deleteScannedAll(scanned: Boolean)

    @Query("UPDATE resultScanModel SET isCheck = :isCheck WHERE id=:id AND scanned=:scanned")
    suspend fun updateCheckItemScanned(id: Int, isCheck: Boolean, scanned: Boolean)


    //list history
    @Query("SELECT * FROM resultScanModel  ORDER BY id DESC ")
    fun getAllResult(): Flow<List<ResultScanModel>>

    @Query("SELECT * FROM resultScanModel WHERE scanned = :scan ORDER BY id DESC")
    fun getScannedResult(scan: Boolean): Flow<List<ResultScanModel>>

    @Query("SELECT * FROM resultScanModel WHERE created = :create ORDER BY id DESC")
    fun getCreatedResult(create: Boolean): Flow<List<ResultScanModel>>

    @Query("SELECT * FROM resultScanModel WHERE favorite = :favorite ORDER BY id DESC ")
    fun getFavoriteResult(favorite: Boolean): Flow<List<ResultScanModel>>

    @Query("UPDATE resultScanModel SET favorite = :favorite WHERE id =:id")
    suspend fun updateFavorite(id: Int, favorite: Boolean)


}