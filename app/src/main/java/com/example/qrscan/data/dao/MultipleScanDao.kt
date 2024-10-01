package com.example.qrscan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qrscan.data.model.MultipleScanModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MultipleScanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMultipleScan(multipleScanModel: MultipleScanModel)

    @Query("DELETE FROM multipleScanModel WHERE isCheck =:isCheck")
    suspend fun delete(isCheck: Boolean)

    @Query("SELECT * FROM multipleScanModel  ORDER BY id DESC ")
    fun getAllResult(): Flow<List<MultipleScanModel>>

    @Query("DELETE FROM multipleScanModel")
    suspend fun deleteAll()

    @Query("UPDATE multipleScanModel SET isDelete = :isDelete")
    suspend fun updateIsDeleteItem(isDelete: Boolean)

    @Query("UPDATE multipleScanModel SET isCheck = :isCheck")
    suspend fun updateIsCheckItem(isCheck: Boolean)

    @Query("UPDATE multipleScanModel SET isCheck = :isCheck WHERE id=:id")
    suspend fun updateCheckItem(id: Int, isCheck: Boolean)
}